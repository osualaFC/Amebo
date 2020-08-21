 package com.example.amebo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        /** set toolbar title **/
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.login_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Login"
        /** creates a back arrow button **/
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        /** send the user back to the WelcomeActivity when the back arrow button is clicked**/
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this@LoginActivity, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}