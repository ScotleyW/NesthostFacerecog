package com.example.nesthostapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide.with
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage

class DisplayProfileActivity : AppCompatActivity() {

    private lateinit var profileImageView: ImageView
    private lateinit var usernameTextView: TextView
    private lateinit var userId: String
    private val firebaseDatabase = FirebaseDatabase.getInstance("https://loginnesthost-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val storageReference = FirebaseStorage.getInstance().reference
    private lateinit var editProfileButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_profile)

        profileImageView = findViewById(R.id.profileImageView)
        usernameTextView = findViewById(R.id.usernameTextView)
        editProfileButton = findViewById(R.id.editProfileButton)

        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        loadUserProfile()

        editProfileButton.setOnClickListener {
            val intent = Intent(this, UserProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadUserProfile() {
        val userRef = firebaseDatabase.getReference("users").child(userId)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val username = snapshot.child("username").value.toString()
                    val profileImageUrl = snapshot.child("profileImageUrl").value.toString()

                    usernameTextView.text = username

                    // Load profile image using Glide library
                    with(this@DisplayProfileActivity)
                        .load(profileImageUrl)
                        .placeholder(R.drawable.default_profile_image)
                        .error(R.drawable.default_profile_image)
                        .into(profileImageView)

                    Log.d("ProfileImageURL", profileImageUrl) // Log the profile image URL
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error if needed
            }
        })
    }
}
