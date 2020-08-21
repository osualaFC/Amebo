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
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    /**no of pages to view**/
    private val PAGES = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.main_toolbar))


        /** 1B create an instance of ViewPagerFragment adapter**/
        val adapter = ViewPagerAdapter(this)

        /** 1C set your viewpager adapter to the created adapter**/
        viewpager.adapter = adapter

        /** 1D setting the tab layout using tab layout mediator**/
        TabLayoutMediator(tab_layout, viewpager) { t, position ->
            when (position) {
                0 -> t.text = "CHATS"
                1 -> t.text = "SEARCH"
                2 -> t.text = "SETTINGS"
            }
        }.attach()
        /**connect tab layout with viewpager**/

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
                     /** this prevent the user from going back to the welcomeActivity when the top back arrow is clicked**/
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