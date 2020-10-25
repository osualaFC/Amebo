package com.example.amebo.adapters

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.amebo.R
import com.example.amebo.ViewFullImageActivity
import com.example.amebo.WelcomeActivity
import com.example.amebo.model.Chats
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.message_item_left.view.*
import kotlinx.android.synthetic.main.message_item_left.view.text_message_chat
import kotlinx.android.synthetic.main.message_item_left.view.text_seen_chat
import kotlinx.android.synthetic.main.message_item_right.view.*

class ChatsAdapter(
    private val context: Context,
    private val chatList: List<Chats>,
    private  var imageUrl: String =""

): RecyclerView.Adapter<ChatsAdapter.MyViewHolder>() {

    var firebaseUser :FirebaseUser = FirebaseAuth.getInstance().currentUser!!
    
   class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
      //var profile_image = itemView.profile_image
       var text_chat = itemView.text_message_chat
       var image_left = itemView.image_left
       var text_seen = itemView.text_seen_chat
       var image_right = itemView.image_right

    }

    override fun getItemViewType(position: Int): Int {

       return  if(chatList[position].sender.equals(firebaseUser.uid)){
           1
       }
        else{
           0
       }
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): MyViewHolder {

        return if(position == 1){
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.message_item_right, parent, false)
             MyViewHolder(view)
        }else{
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.message_item_left, parent, false)
            MyViewHolder(view)
        }

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val chat: Chats = chatList[position]

        /**profile pic of receiver**/
        //Picasso.get().load(imageUrl).placeholder(R.drawable.ic_profile).into(holder.profile_image)

        /**for image messages***/
        if (chat.message.equals("sent you an image.") && !chat.url.equals("")) {

            /**display sender chat images**/
            if (chat.sender.equals(firebaseUser.uid)) {
                holder.text_chat.visibility = View.GONE
                holder.image_right.visibility = View.VISIBLE
                Picasso.get().load(chat.url).into(holder.image_right)

                /***handle sent images**/
                holder.image_right.setOnClickListener {
                    val options = arrayOf<CharSequence>(
                        "View Full Image",
                        "Delete Image",
                        "Cancel"
                    )
                    val builder:AlertDialog.Builder = AlertDialog.Builder(context)
                    builder.setTitle("Actions")
                    builder.setItems(options, DialogInterface.OnClickListener { dialog, position ->
                        when(position){
                            0 ->{
                                val intent = Intent(context, ViewFullImageActivity::class.java)
                                intent.putExtra("url", chat.url)
                                context.startActivity(intent)
                            }
                            1-> {
                                deleteSentMessages(position, holder)
                            }
                        }
                    })
                    builder.show()
                }
            }
            /**display receiver chat images**/
            else if (!chat.sender.equals(firebaseUser.uid)) {
                holder.text_chat.visibility = View.GONE
                holder.image_left.visibility = View.VISIBLE
                Picasso.get().load(chat.url).into(holder.image_left)

                /***handle received images**/
                holder.image_left.setOnClickListener {
                    val options = arrayOf<CharSequence>(
                        "View Full Image",
                        "Cancel"
                    )
                    val builder:AlertDialog.Builder = AlertDialog.Builder(context)
                    builder.setTitle("Actions")
                    builder.setItems(options, DialogInterface.OnClickListener { dialog, position ->
                        when(position){
                            0 ->{
                                val intent = Intent(context, ViewFullImageActivity::class.java)
                                intent.putExtra("url", chat.url)
                                context.startActivity(intent)
                            }

                        }
                    })
                    builder.show()
                }
            }
        }
        /**for text messages**/
        else {
            holder.text_chat.text = chat.message

            /***handle chat messages**/
            holder.text_chat.setOnClickListener {
                val options = arrayOf<CharSequence>(
                    "Delete Message",
                    "Cancel"
                )
                val builder:AlertDialog.Builder = AlertDialog.Builder(context)
                builder.setTitle("Actions")
                builder.setItems(options, DialogInterface.OnClickListener { dialog, position ->
                    when(position){
                        0 ->{
                            deleteSentMessages(position, holder)
                        }
                    }
                })
                builder.show()
            }
        }

        /**for sent and seen indicator**/
        if (position == chatList.size - 1) {
            if (chat.isseen) {
                holder.text_seen.text = "Seen"
                if (chat.message.equals("sent you an image.") && !chat.url.equals("")) {

                    /**adjust position of seen text**/
                    val lp: RelativeLayout.LayoutParams =
                        holder.text_seen.layoutParams as RelativeLayout.LayoutParams
                    lp.setMargins(0, 245, 10, 0)
                    holder.text_seen.layoutParams = lp
                } else {
                    holder.text_seen.text = "Sent"
                    if (chat.message.equals("sent you an image.") && !chat.url.equals("")) {

                        /**adjust position of seen text**/
                        val lp: RelativeLayout.LayoutParams =
                            holder.text_seen.layoutParams as RelativeLayout.LayoutParams
                        lp.setMargins(0, 245, 10, 0)
                        holder.text_seen.layoutParams = lp
                    }
                }
            } else {
                holder.text_seen.visibility = View.GONE
            }


        }

    }
    override fun getItemCount(): Int{
        return chatList.size
    }

    /**delete sent message***/
    private fun deleteSentMessages(position: Int, holder: ChatsAdapter.MyViewHolder){
        val ref = FirebaseDatabase.getInstance().reference.child("Chats")
            .child(chatList[position].messageId).removeValue()
            .addOnCompleteListener{task->
                if(task.isSuccessful){
                    Toast.makeText(context, "Message deleted", Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(context, "Not deleted successfully", Toast.LENGTH_SHORT).show()
                }

            }
    }
    
}