package com.example.amebo.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.amebo.R
import com.example.amebo.adapters.UserAdapter
import com.example.amebo.model.ChatList
import com.example.amebo.model.Users
import com.example.amebo.notifications.Token
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.fragment_chat.view.*


class ChatFragment : Fragment() {

    private var userAdapter: UserAdapter? = null
    private var mUsers: List<Users>? = null
    private var userChatList: List<ChatList>? = null
    lateinit var recyclerView: RecyclerView
    private var firebaseUser: FirebaseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        /***set up recycler view**/
        recyclerView = view.chat_list_rv
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)

        /**get current user**/
        firebaseUser = FirebaseAuth.getInstance().currentUser
        userChatList = ArrayList()

        val chatRef = FirebaseDatabase.getInstance().reference.child("ChatList").child(firebaseUser!!.uid)
        chatRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                (userChatList as ArrayList).clear()
                for(snapshot in p0.children){
                    val chatList = snapshot.getValue(ChatList::class.java)
                    (userChatList as ArrayList).add(chatList!!)
                }
                retrieveChatList()
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


        /**18 update token**/
         updateToken(FirebaseInstanceId.getInstance().token)



        return view
    }

    private fun updateToken(token: String?) {
        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
        val token1 = Token(token!!)
        ref.child(firebaseUser!!.uid).setValue(token1)
    }

    /**13 retrieve chat list**/
private fun retrieveChatList(){

        mUsers = ArrayList()

        val ref = FirebaseDatabase.getInstance().reference.child("Users")
        ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                (mUsers as ArrayList).clear()

                for(snapshot in p0.children){

                    val user = snapshot.getValue(Users::class.java)

                    for(friend in userChatList!!){
                        if(user!!.uid.equals(friend.id)){
                            (mUsers as ArrayList).add(user!!)
                        }
                    }
                }
                userAdapter = UserAdapter(context!!, (mUsers as ArrayList<Users>), true)
                recyclerView.adapter = userAdapter
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

}
}