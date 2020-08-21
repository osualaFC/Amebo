package com.example.amebo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        /** set toolbar title **/
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.register_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Register"
        /** creates a back arrow button **/
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        /** send the user back to the WelcomeActivity when the back arrow button is clicked**/
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this@RegisterActivity, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}