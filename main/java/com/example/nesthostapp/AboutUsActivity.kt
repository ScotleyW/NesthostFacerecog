package com.example.nesthostapp

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AboutUsActivity : AppCompatActivity() {

    private lateinit var aboutTextView: TextView
    private lateinit var birdImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)

        aboutTextView = findViewById(R.id.about_text_view)
        birdImageView = findViewById(R.id.bird_image_view)

        val description = "NestHost is a smart home device that utilizes a mobile app for easy access and a face recognition system. We are passionate about creating innovative solutions that make your home smarter and more convenient. With NestHost, you can enjoy a seamless and personalized home automation experience."

        // Set the description text
        aboutTextView.text = description

        // Apply fading animation
        applyFadingAnimation()

        // Apply bird flying animation
        applyBirdFlyingAnimation()
    }

    private fun applyFadingAnimation() {
        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.duration = 1000
        fadeIn.startOffset = 500

        val fadeOut = AlphaAnimation(1f, 0f)
        fadeOut.duration = 1000
        fadeOut.startOffset = 3000

        val animation = AnimationSet(true)
        animation.addAnimation(fadeIn)
        animation.addAnimation(fadeOut)

        aboutTextView.animation = animation

        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationRepeat(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                // Start the animation again after it ends
                aboutTextView.startAnimation(animation)
            }
        })

        // Start the animation
        aboutTextView.startAnimation(animation)
    }

    private fun applyBirdFlyingAnimation() {
        val screenWidth = resources.displayMetrics.widthPixels

        // Calculate the translation distance for bird's flight path
        val translationDistance = screenWidth + birdImageView.width

        // Create an ObjectAnimator for bird's translation along the x-axis
        val birdAnimator = ObjectAnimator.ofFloat(
            birdImageView,
            "translationX",
            0f,
            -translationDistance.toFloat()
        )
        birdAnimator.duration = 10000 // Adjust the duration as needed
        birdAnimator.repeatCount = ObjectAnimator.INFINITE
        birdAnimator.interpolator = DecelerateInterpolator()

        // Start the bird's flying animation
        birdAnimator.start()
    }
}
