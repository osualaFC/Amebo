package com.example.amebo

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
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    /**no of pages to view**/
    private val PAGES = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) 
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.main_toolbar))

        /** set toolbar title to null.. this is to display the username and profile pics as the toolbar title**/
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.main_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = ""



        /** 1B create an instance of ViewPagerFragment adapter**/
        val adapter = ViewPagerAdapter(this)

        /** 1C set your viewpager adapter to the created adapter**/
        viewpager.adapter = adapter

        /** 1D setting the tab layout using tab layout mediator**/
        TabLayoutMediator(tab_layout, viewpager){ t, position ->
            when(position){
                0 -> t.text = "CHATS"
                1 -> t.text = "SEARCH"
                2 -> t.text = "SETTINGS"
            }
        }.attach() /**connect tab layout with viewpager**/

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
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
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