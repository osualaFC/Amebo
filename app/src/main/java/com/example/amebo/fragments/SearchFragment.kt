package com.example.amebo.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.amebo.R
import com.example.amebo.adapters.UserAdapter
import com.example.amebo.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_search.*

/***8A display all users and search for specific user**/
class SearchFragment : Fragment() {

    private var userAdapter: UserAdapter? = null
    private var mUsers: List<Users>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search, container, false)


        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        /**set up recyclerview layout**/
        //searchList.hasFixedSize()
        searchList.layoutManager = LinearLayoutManager(requireContext())

        mUsers = ArrayList()
        /**retrieve all users**/
        retrieveAllUsers()
    }

//        /**search edit text listener**/
//        search_for_user.addTextChangedListener(object: TextWatcher{
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                p0
//            }
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                searchForUser(p0.toString().toLowerCase())
//            }
//
//            override fun afterTextChanged(p0: Editable?) {
//                p0
//            }
//        })
//    }

    fun retrieveAllUsers() {
        var firebaseUserID = FirebaseAuth.getInstance().currentUser?.uid
        /** retrieve all users**/
        var refUsers = FirebaseDatabase.getInstance().reference.child("Users")

        refUsers.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                /**clear the arrayList**/
                (mUsers as ArrayList<Users>).clear()

              // if(search_for_user!!.text.toString() == ""){
                   /**loop thru the users data**/
                   for(snapShots in p0.children){

                       val user: Users? = snapShots.getValue(Users::class.java)
                       /**user cannot search for his/her own name**/
                       if(!(user?.uid).equals(firebaseUserID)){
                           (mUsers as ArrayList<Users>).add(user!!)
                       }
                   }
              // }
                userAdapter = UserAdapter(context!!, mUsers!!, false)

                searchList.adapter = userAdapter
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

        /**8B search func to display list the user searched for**/
        fun searchForUser(str: String) {
            val firebaseUserID = FirebaseAuth.getInstance().currentUser?.uid

            /** retrieve all users**/
            val queryUsers =
                FirebaseDatabase.getInstance().reference.child("Users").orderByChild("search")
                    .startAt(str)
                    .endAt(str + "\uf8ff")

            queryUsers.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    /**search and display all users with the "str" as name**/
                    (mUsers as ArrayList<Users>).clear()

                    /**loop thru the users data**/
                    for (snapShots in p0.children) {

                        val user: Users? = snapShots.getValue(Users::class.java)
                        /**user cannot search for his/her own name**/
                        if (!(user?.uid).equals(firebaseUserID)) {
                            (mUsers as ArrayList<Users>).add(user!!)
                        }
                    }
                    userAdapter = UserAdapter(context!!, mUsers!!, false)
                }

                override fun onCancelled(p0: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }

}