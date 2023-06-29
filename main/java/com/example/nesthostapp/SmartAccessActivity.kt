package com.example.nesthostapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class SmartAccessActivity : AppCompatActivity() {

    private lateinit var led1Switch: Switch
    private lateinit var led2Switch: Switch
    private lateinit var led3Switch: Switch
    private lateinit var servoButton: Button
    private lateinit var textViewUsernameValue: TextView

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var username: String = "Unknown User"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_smart_access)

        database = FirebaseDatabase.getInstance("https://loginnesthost-default-rtdb.asia-southeast1.firebasedatabase.app/").reference
        auth = FirebaseAuth.getInstance()

        textViewUsernameValue = findViewById(R.id.textViewUsernameValue)
        led1Switch = findViewById(R.id.led1Switch)
        led2Switch = findViewById(R.id.led2Switch)
        led3Switch = findViewById(R.id.led3Switch)
        servoButton = findViewById(R.id.servoButton)

        val currentUser = auth.currentUser
        if (currentUser != null) {
            textViewUsernameValue.text = currentUser.displayName
            val userId = currentUser.uid
            val userRef = database.child("users").child(userId)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    username = snapshot.child("username").getValue(String::class.java) ?: "Unknown User"
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database read error
                }
            })
        }

        setupLedSwitches()
        setupServoButton()
    }

    private fun setupLedSwitches() {
        led1Switch.setOnCheckedChangeListener { _, isChecked ->
            val led = "LED1"
            val action = if (isChecked) "on" else "off"
            val statusMessage = "$username turned $led $action"
            updateLedState("led1", isChecked, statusMessage)
        }

        led2Switch.setOnCheckedChangeListener { _, isChecked ->
            val led = "LED2"
            val action = if (isChecked) "on" else "off"
            val statusMessage = "$username turned $led $action"
            updateLedState("led2", isChecked, statusMessage)
        }

        led3Switch.setOnCheckedChangeListener { _, isChecked ->
            val led = "LED3"
            val action = if (isChecked) "on" else "off"
            val statusMessage = "$username turned $led $action"
            updateLedState("led3", isChecked, statusMessage)
        }

        database.child("leds").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val led1State = snapshot.child("led1").getValue(Boolean::class.java) ?: false
                val led2State = snapshot.child("led2").getValue(Boolean::class.java) ?: false
                val led3State = snapshot.child("led3").getValue(Boolean::class.java) ?: false

                led1Switch.isChecked = led1State
                led2Switch.isChecked = led2State
                led3Switch.isChecked = led3State
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database read error
            }
        })
    }

    private fun setupServoButton() {
        servoButton.setOnClickListener {
            val servoPosition = "90" // Replace with the desired servo position value
            val statusMessage = "$username has arrived home"
            updateServoPosition(servoPosition, statusMessage)
            sendServoCommand("on")
        }
    }

    private fun updateLedState(led: String, state: Boolean, statusMessage: String) {
        val ledState = if (state) "on" else "off"
        val command = "$led:$ledState"

        database.child("commands").setValue(command)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@SmartAccessActivity, "Led $led is ${if (state) "on" else "off"}", Toast.LENGTH_SHORT).show()
                    saveStatusMessage(statusMessage)
                } else {
                    Toast.makeText(this@SmartAccessActivity, "Failed to update led state", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun updateServoPosition(servoPosition: String, statusMessage: String) {
        database.child("servoPosition").setValue(servoPosition)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@SmartAccessActivity, "Servo position updated", Toast.LENGTH_SHORT).show()
                    saveStatusMessage(statusMessage)
                } else {
                    Toast.makeText(this@SmartAccessActivity, "Failed to update servo position", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun sendServoCommand(commands: String) {
        val commandRef = database.child("commands").child("servo")
        commandRef.setValue(commands)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@SmartAccessActivity, "Servo command sent: $commands", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@SmartAccessActivity, "Failed to send servo commands", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveStatusMessage(statusMessage: String) {
        val statusMessagesRef = database.child("status_messages")
        val newStatusMessageRef = statusMessagesRef.push()
        newStatusMessageRef.setValue(statusMessage)
    }
}


