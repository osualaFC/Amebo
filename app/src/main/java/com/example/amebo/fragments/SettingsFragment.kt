package com.example.amebo.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.amebo.R
import com.example.amebo.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_settings.view.*


class SettingsFragment : Fragment() {

    var userReference:DatabaseReference? = null
    var firebaseUser:FirebaseUser? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        userReference = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        /**9 retrieve data from database for user settings***/
        userReference!!.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
               if(p0.exists()){
                   val user: Users? =p0.getValue(Users::class.java)

                   if(context != null){
                       view.username_settings.text = user?.userName
                       Picasso.get().load(user?.profile).placeholder(R.drawable.user_icon).into(view.profile_image_settings)
                       Picasso.get().load(user?.cover).placeholder(R.drawable.cover).into(view.cover_image_settings)
                   }


               }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        return view
    }


}