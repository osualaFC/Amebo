package com.example.amebo.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.amebo.ui.MessagingActivity
import com.example.amebo.R
import com.example.amebo.ui.VisitUserProfileActivity
import com.example.amebo.model.Chats
import com.example.amebo.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.user_search_item_layout.view.*
/**8 adapter for search fragment**/
class UserAdapter(
    private val context: Context,
    private val mUsers :List<Users>,
    private val isChatChecked:Boolean
    ) : RecyclerView.Adapter<UserAdapter.ViewHolder>(){

    var lastMsg: String? = null

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        /**initialize variables**/
         var userNameTxt: TextView = itemView.user_name
        var profileImageView: CircleImageView = itemView.profile_image
        var onLineTxt: CircleImageView = itemView.status_online
        var offLineTxt: CircleImageView = itemView.status_offline
        var lastMessage: TextView = itemView.message_last

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val view = LayoutInflater.from(context).inflate(R.layout.user_search_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user: Users? = mUsers[position]
        holder.userNameTxt.text = user?.userName
       Picasso.get().load(user?.profile).placeholder(R.drawable.ic_profile).into(holder.profileImageView)

        /** retrieve last message and status check**/
        if(isChatChecked){
            retrieveLastMessage(user!!.uid, holder.lastMessage)
            if(user.status == "online"){
                holder.onLineTxt.visibility = View.VISIBLE
                holder.offLineTxt.visibility = View.GONE
            }
            else{
                holder.onLineTxt.visibility = View.GONE
                holder.offLineTxt.visibility = View.VISIBLE
            }
        }
        else{
            holder.lastMessage.visibility = View.GONE
            holder.onLineTxt.visibility = View.GONE
            holder.offLineTxt.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            val options = arrayOf<CharSequence>(
                "Send message",
                "Visit profile"
            )
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setTitle("Checkout ${user?.userName}")
            builder.setItems(options, DialogInterface.OnClickListener{dialog, position ->
                    if(position == 0){
                        val intent = Intent(context, MessagingActivity::class.java)
                        intent.putExtra("chat_with", user?.uid)
                        context.startActivity(intent)
                    }
                if(position == 1){
                    val intent = Intent(context, VisitUserProfileActivity::class.java)
                    intent.putExtra("chat_with", user?.uid)
                    context.startActivity(intent)
                }
            })
            builder.show()
        }
    }


    override fun getItemCount(): Int = mUsers.size

    private fun retrieveLastMessage(uid: String, lastMessage: TextView) {
        lastMsg ="default message"

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val ref = FirebaseDatabase.getInstance().reference.child("Chats")

        ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                for(snapshot in p0.children){

                    val chat: Chats? = snapshot.getValue(Chats::class.java)

                    if(firebaseUser != null && chat != null){
                        if(chat.receiver == firebaseUser!!.uid && chat.sender == uid
                            ||chat.receiver ==uid  && chat.sender == firebaseUser!!.uid
                        ){
                            lastMsg = chat.message
                        }

                    }
                }

                when(lastMsg){
                    "default message" -> lastMessage.text = "No message"
                    "sent you an image." -> lastMessage.text ="Image sent"
                    else -> lastMessage.text = lastMsg
                }
                lastMsg = "default message"
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}