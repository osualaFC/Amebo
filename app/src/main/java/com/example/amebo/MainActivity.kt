package com.example.amebo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.amebo.fragments.ChatFragment
import com.example.amebo.fragments.SearchFragment
import com.example.amebo.fragments.SettingsFragment
import com.example.amebo.model.Chats
import com.example.amebo.model.Users
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_app_bar_layout.*

class MainActivity : AppCompatActivity() {

    /**no of pages to view**/
    private val PAGES = 3
    private var unreadCount: Int = 0

    /**7 get and display user name and image**/
    /**7A reference database and user**/
    var refUsers : DatabaseReference? = null
    var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.main_toolbar))

        /**16 get unread messages count**/
        val ref =FirebaseDatabase.getInstance().reference.child("Chats")
        ref!!.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {

                unreadCount = 0

                for(snapshot in p0.children){
                    val chat = snapshot.getValue(Chats::class.java)
                    if(chat!!.receiver.equals(firebaseUser!!.uid) && !chat.isseen){
                        unreadCount+=1
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        /** 1B create an instance of ViewPagerFragment adapter**/
        val adapter = ViewPagerAdapter(this)

        /** 1C set your viewpager adapter to the created adapter**/
        viewpager.adapter = adapter

        /** 1D setting the tab layout using tab layout mediator**/
        TabLayoutMediator(tab_layout, viewpager) { t, position ->
            when (position) {
                0 -> t.text = if(unreadCount == 0)"CHATS" else "$unreadCount CHATS"
                1 -> t.text = "SEARCH"
                2 -> t.text = "SETTINGS"
            }
        }.attach()
        /**connect tab layout with viewpager**/



       /**7B get instance of user from the db**/
        firebaseUser = FirebaseAuth.getInstance().currentUser
        refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        /**7C display user name and profile picture**/
        refUsers!!.addValueEventListener(object :ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
               /**check if user is present in the db  (create ur Users model) */
                if(p0.exists()){

                   val user: Users? = p0.getValue(Users::class.java)
                   /**set name and profile views with the data from db**/
                   username.text = user?.userName
                  Picasso.get().load(user?.profile).placeholder(R.drawable.ic_profile).into(profile_pic)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.logout -> {
                /** 6A sign user out when he clicks the sign_out button in the menu layout**/
                FirebaseAuth.getInstance().signOut()
                /** 6B send user back to welcomeActivity**/

                     /** send user to welcome activity **/
                val intent = Intent(this@MainActivity, WelcomeActivity::class.java)
                     /** this prevent the user from going back to the mainActivity when the top back arrow is clicked**/
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()


                return true
            }

        }
        return false
    }


    /** 1A viewpager adapter**/

    private inner class ViewPagerAdapter(frag : FragmentActivity): FragmentStateAdapter(frag){



        override fun getItemCount(): Int= PAGES


        override fun createFragment(position: Int): Fragment {
           return when(position){
               0 -> ChatFragment()
               1 -> SearchFragment()
               else -> SettingsFragment()
           }
        }


    }
}