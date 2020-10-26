package com.example.amebo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    /** 4A initialize firebase auth **/
    private lateinit var mAuth: FirebaseAuth
    /**4B reference to database**/
    private lateinit var refUsers: DatabaseReference
    /**firebase userID**/
    private var firebaseUserID = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        /** set toolbar title **/
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.register_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = ""
        supportActionBar!!.elevation = 0F
        /** creates a back arrow button **/
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        /**3B send the user back to the WelcomeActivity when the back arrow button is clicked**/
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this@RegisterActivity, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }


        mAuth = FirebaseAuth.getInstance()
        /**4D validate and register user details  **/
        register_button.setOnClickListener {
            registerUser()
        }
    }

    /**4C register user function**/
    private fun registerUser(){
        /** get users details **/
        val userName = register_username.editText?.text.toString()
        val userEmail = register_email.editText?.text.toString()
        val userPassword = register_password.editText?.text.toString()

        /** validation **/
        if(userName == ""){
            Toast.makeText(this@RegisterActivity, "username cannot be empty", Toast.LENGTH_LONG).show()
        }else if(userEmail == ""){
            Toast.makeText(this@RegisterActivity, "Use correct email format", Toast.LENGTH_LONG).show()
        }else if(userPassword == ""){
            Toast.makeText(this@RegisterActivity, "password cannot be empty", Toast.LENGTH_LONG).show()
        }
        else{
            /** authenticate user  **/
            mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener {
                    /** if authentication is successful**/
                    if(it.isSuccessful){
                        /** add user to the data base **/
                        firebaseUserID = mAuth.currentUser?.uid.toString()
                        refUsers =FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUserID)

                        var userHashMap = HashMap<String, Any>()
                            userHashMap["uid"] = firebaseUserID
                            userHashMap["userName"] = userName
                            userHashMap["profile"] = "https://firebasestorage.googleapis.com/v0/b/ameboapp-b841a.appspot.com/o/profile.png?alt=media&token=b71a9faf-b11c-475c-ac78-92a107898c3f"
                            userHashMap["cover"] = "https://firebasestorage.googleapis.com/v0/b/ameboapp-b841a.appspot.com/o/cover.jpeg?alt=media&token=07352e74-b17c-4d15-9dbf-96c457705dc4"
                            userHashMap["status"] = "offline"
                            userHashMap["search"] = userName.toLowerCase()
                            userHashMap["facebook"] = "https://m.facebook.com"
                            userHashMap["instagram"] = "https://m.instagram.com"

                            userHashMap["website"] = "https://www.google.com"

                        /** update database **/
                        refUsers.updateChildren(userHashMap)
                            .addOnCompleteListener {task ->
                                if(task.isSuccessful){
                                    /** send user to main activity **/
                                    val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                                    /** this prevent the user from going back to the welcomeActivity when the top back arrow is clicked**/
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                    }else{
                        Toast.makeText(this@RegisterActivity, it.exception?.message.toString(), Toast.LENGTH_LONG).show()
                    }
                }

        }
    }

}