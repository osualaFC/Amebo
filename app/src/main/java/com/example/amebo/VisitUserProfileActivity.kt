package com.example.amebo

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.amebo.model.Users
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_visit_user_profile.*

class VisitUserProfileActivity : AppCompatActivity() {

    private var userId: String? = null
    private var user:Users? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visit_user_profile)

        userId  = intent.getStringExtra("chat_with")

        val ref = FirebaseDatabase.getInstance().reference.child("Users").child(userId!!)

        ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    user = p0.getValue(Users::class.java)

                    username_visit_settings.text = user!!.userName
                    Picasso.get().load(user?.profile).into(profile_image_visit_settings)
                   // Picasso.get().load(user?.cover).into(cover_image_visit_settings)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        /**visit user links**/
        set_fb_visit.setOnClickListener {
            val uri = Uri.parse(user?.facebook)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        set_ig_visit.setOnClickListener {
            val uri = Uri.parse(user?.instagram)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
        set_web_visit.setOnClickListener {
            val uri = Uri.parse(user?.website)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        send_msg.setOnClickListener {
            val intent = Intent(this@VisitUserProfileActivity, MessagingActivity::class.java)
            intent.putExtra("chat_with", user?.uid)
            startActivity(intent)
            finish()
        }
    }
}