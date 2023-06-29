package com.example.nesthostapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView

class MainActivity : Activity() {
    private var _bg__homepage_ek2: View? = null
    private var untitled411_20230607174905_1: ImageView? = null
    private var vector: ImageView? = null
    private var vector_ek1: ImageView? = null
    private var vector_ek2: ImageView? = null
    private var vector_ek3: ImageView? = null
    private var vector_ek4: ImageView? = null
    private var vector_ek5: ImageView? = null
    private var vector_ek6: ImageView? = null
    private var vector_ek7: ImageView? = null
    private var vector_ek8: ImageButton? = null
    private var vector_ek9: ImageButton? = null
    private var vector_ek10: ImageButton? = null
    private var vector_ek11: ImageButton? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        _bg__homepage_ek2 = findViewById(R.id._bg__homepage_ek2) as View
        untitled411_20230607174905_1 =
            findViewById<View>(R.id.untitled411_20230607174905_1) as ImageView
        vector = findViewById<View>(R.id.vector) as ImageView
        vector_ek1 = findViewById<View>(R.id.vector_ek1) as ImageView
        vector_ek2 = findViewById<View>(R.id.vector_ek2) as ImageView
        vector_ek3 = findViewById<View>(R.id.vector_ek3) as ImageView
        vector_ek4 = findViewById<View>(R.id.vector_ek4) as ImageView
        vector_ek5 = findViewById<View>(R.id.vector_ek5) as ImageView
        vector_ek6 = findViewById<View>(R.id.vector_ek6) as ImageView
        vector_ek7 = findViewById<View>(R.id.vector_ek7) as ImageView
        vector_ek8 = findViewById<View>(R.id.vector_ek8) as ImageButton
        vector_ek9 = findViewById<View>(R.id.vector_ek9) as ImageButton
        vector_ek10 = findViewById<View>(R.id.vector_ek10) as ImageButton
        vector_ek11 = findViewById<View>(R.id.vector_ek11) as ImageButton

        vector_ek8?.setOnClickListener {
            val intent = Intent(this, AboutUsActivity::class.java)
            startActivity(intent)
        }

        vector_ek9?.setOnClickListener {
            val intent = Intent(this, StatusMessageActivity::class.java)
            startActivity(intent)
        }

        vector_ek10?.setOnClickListener {
            val intent = Intent(this, SmartAccessActivity::class.java)
            startActivity(intent)
        }

        vector_ek11?.setOnClickListener {
            val intent = Intent(this, DisplayProfileActivity::class.java)
            startActivity(intent)
        }
    }
}
