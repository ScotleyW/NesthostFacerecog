package com.example.nesthostapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class StatusMessageActivity : AppCompatActivity() {

    private lateinit var listViewStatusMessages: ListView
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private val statusMessagesList = mutableListOf<String>()
    private lateinit var statusMessagesAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status_message)

        listViewStatusMessages = findViewById(R.id.listViewStatusMessages)
        database = FirebaseDatabase.getInstance("https://loginnesthost-default-rtdb.asia-southeast1.firebasedatabase.app/").reference
        auth = FirebaseAuth.getInstance()

        statusMessagesAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, statusMessagesList)
        listViewStatusMessages.adapter = statusMessagesAdapter

        fetchStatusMessages()
    }

    private fun fetchStatusMessages() {
        val query = database.child("status_messages").orderByKey().limitToLast(10)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                statusMessagesList.clear()

                for (childSnapshot in snapshot.children.reversed()) {
                    val statusMessage = childSnapshot.getValue(String::class.java)
                    statusMessage?.let {
                        statusMessagesList.add(it)
                    }
                }

                statusMessagesAdapter.notifyDataSetChanged()
                scrollListViewToBottom()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database read error
            }
        })
    }

    private fun scrollListViewToBottom() {
        listViewStatusMessages.post {
            listViewStatusMessages.setSelection(listViewStatusMessages.count - 1)
        }
    }
}
