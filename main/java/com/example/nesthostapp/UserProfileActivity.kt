package com.example.nesthostapp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class UserProfileActivity : AppCompatActivity() {

    private lateinit var profileImageView: ImageView
    private lateinit var usernameEditText: EditText
    private lateinit var houseSpinner: Spinner
    private lateinit var addHouseEditText: EditText
    private val firebaseDatabase = FirebaseDatabase.getInstance("https://loginnesthost-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val firebaseStorage = FirebaseStorage.getInstance()
    private val storageReference = firebaseStorage.reference

    private val RequestImageCapture: Int
        get() = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        profileImageView = findViewById(R.id.profileImageView)
        usernameEditText = findViewById(R.id.usernameEditText)
        houseSpinner = findViewById(R.id.houseSpinner)
        addHouseEditText = findViewById(R.id.addHouseEditText)
        val saveButton: Button = findViewById(R.id.saveButton)
        val addHouseButton: Button = findViewById(R.id.addHouseButton)

        // Set click listener on profile image to capture a new image
        profileImageView.setOnClickListener {
            dispatchTakePictureIntent()
        }

        // Set click listener on save button to save the changes
        saveButton.setOnClickListener {
            saveUserProfile()
        }

        // Set click listener on add house button to add a new house
        addHouseButton.setOnClickListener {
            addNewHouse()
        }

        // Populate the spinner with existing houses
        populateHouseSpinner()
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, RequestImageCapture)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RequestImageCapture && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            profileImageView.setImageBitmap(imageBitmap)
        }
    }

    private fun saveUserProfile() {
        val username = usernameEditText.text.toString()
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid



        if (userId != null) {
            val userRef = firebaseDatabase.getReference("users").child(userId)
            userRef.child("username").setValue(username)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Profile saved successfully
                        uploadProfileImage(userId)
                        showSnackbar("Profile saved successfully")
                        navigateToHomeScreen()
                    } else {
                        // Failed to save profile
                        showSnackbar("Failed to save profile. Please try again.")
                        // You can also log the error for debugging purposes
                        Log.e(TAG, "Failed to save profile", task.exception)
                    }
                }
        }
    }

    private fun uploadProfileImage(userId: String) {
        val storageReference = storageReference.child("profile_images/$userId.jpg")
        val imageBitmap = profileImageView.drawable.toBitmap()

        val baos = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = storageReference.putBytes(data)
        uploadTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Image uploaded successfully
                // You can perform any additional actions or display a success message
            } else {
                // Failed to upload image
                // You can display an error message or handle the failure
                Log.e(TAG, "Failed to upload image", task.exception)
            }
        }
    }

    private fun showSnackbar(message: String) {
        val rootView: View = findViewById(android.R.id.content)
        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun navigateToHomeScreen() {
        // Add your code to navigate to the home screen or perform any other actions
    }

    private fun populateHouseSpinner() {
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid

        if (userId != null) {
            val userHousesRef = firebaseDatabase.getReference("user_houses")
            userHousesRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val houseList = mutableListOf<String>()

                    // Add existing houses to the list
                    for (houseSnapshot in snapshot.children) {
                        val houseName = houseSnapshot.key.toString()
                        val userList = houseSnapshot.value as List<String>
                        val userCount = userList.size
                        houseList.add("$houseName ($userCount)")
                    }

                    // Create an ArrayAdapter to populate the spinner
                    val adapter = ArrayAdapter(this@UserProfileActivity, android.R.layout.simple_spinner_item, houseList)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    houseSpinner.adapter = adapter
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database read error
                }
            })
        }
    }

    private fun addNewHouse() {
        val houseName = addHouseEditText.text.toString()
        if (houseName.isNotEmpty()) {
            val user = FirebaseAuth.getInstance().currentUser
            val userId = user?.uid

            if (userId != null) {
                val userHousesRef = firebaseDatabase.getReference("user_houses")
                userHousesRef.child(houseName).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val userList = mutableListOf<String>()
                        if (snapshot.exists()) {
                            // House already exists, fetch existing user list
                            val existingUserList = snapshot.value as List<String>
                            userList.addAll(existingUserList)
                        }

                        // Add the current user to the user list for the house
                        userList.add(userId)

                        // Update the user list for the house in the database
                        userHousesRef.child(houseName).setValue(userList)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    // House added/updated successfully
                                    showSnackbar("New house added: $houseName")
                                    // Clear the input field
                                    addHouseEditText.setText("")
                                } else {
                                    // Failed to add/update house
                                    showSnackbar("Failed to add new house. Please try again.")
                                    // You can also log the error for debugging purposes
                                    Log.e(TAG, "Failed to add house", task.exception)
                                }
                            }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle database read error
                    }
                })
            }
        }
    }

    companion object {
        private const val TAG = "UserProfileActivity"
    }
}
