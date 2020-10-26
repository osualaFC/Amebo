 package com.example.amebo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

 class LoginActivity : AppCompatActivity() {

    /** 5A initialize firebase auth **/
    private lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        /** set toolbar title **/
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.login_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = ""
        /** creates a back arrow button **/
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        /**3A send the user back to the WelcomeActivity when the back arrow button is clicked**/
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this@LoginActivity, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }


        mAuth = FirebaseAuth.getInstance()
        login_button.setOnClickListener {
            loginUser()
        }
    }

     /**5B login user function **/
     private fun loginUser(){
         /** get user details**/
         val userEmail = login_email.editText?.text.toString()
         val userPassword = login_password.editText?.text.toString()

         /** validation**/
         if(userEmail == ""){
             Toast.makeText(this@LoginActivity, "Use correct email format", Toast.LENGTH_LONG).show()
         }else if(userPassword == ""){
             Toast.makeText(this@LoginActivity, "password cannot be empty", Toast.LENGTH_LONG).show()
         }else{
             /** sign in the user  **/
             mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                 .addOnCompleteListener {task ->
                     if(task.isSuccessful){
                         /** send user to main activity **/
                         val intent = Intent(this@LoginActivity, MainActivity::class.java)
                         /** this prevent the user from going back to the welcomeActivity when the top back arrow is clicked**/
                         intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                         startActivity(intent)
                         finish()
                     }
                     else{
                         Toast.makeText(this@LoginActivity, task.exception?.message.toString(), Toast.LENGTH_LONG).show()
                     }
                 }
         }
     }
}