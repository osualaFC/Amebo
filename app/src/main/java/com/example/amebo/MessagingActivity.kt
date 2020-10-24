package com.example.amebo

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.amebo.adapters.ChatsAdapter
import com.example.amebo.model.Chats
import com.example.amebo.model.Users
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_messaging.*

class MessagingActivity : AppCompatActivity() {

    var chatUserId: String? = ""
    var firebaseUser: FirebaseUser? = null
    val RequestCode = 1
    var chatAdapter: ChatsAdapter? = null
    var chatList:List<Chats>? = null
    lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messaging)

        chatUserId = intent.getStringExtra("chat_with")

        firebaseUser = FirebaseAuth.getInstance().currentUser

        /***12C setup recylerview**/
        recyclerView = messaging_rv
        recyclerView.setHasFixedSize(true)
        var linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager


        /***11b display receiver details**/
        val reference = FirebaseDatabase.getInstance().reference.child("Users").child(chatUserId!!)
        reference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                val user: Users? = p0.getValue(Users::class.java)

                messaging_username.text = user!!.userName
                Picasso.get().load(user.profile).into(messaging_profile_img)

                /**retrieve messages**/
                retrieveMessages(firebaseUser!!.uid, chatUserId, user.profile)
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


        /***11 send a chat**/
        send_message_btn.setOnClickListener {
            val message = chat_message.text.toString()
            if(message.isEmpty()){
                return@setOnClickListener
            }
            else{
                sendMessageToChat(firebaseUser!!.uid, chatUserId, message)
            }
            chat_message.setText("")
        }

        /**11c send files to receiver**/
        attach_image_btn.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "select Image"),RequestCode )
        }
    }

    /***store chat messages in firebase**/
    private fun sendMessageToChat(senderId: String, recieverId: String?, message:String){


        val reference = FirebaseDatabase.getInstance().reference
        /**generate unique key for each message**/
        val messageKey = reference.push().key

        val messageHashMap = HashMap<String, Any?>()
        messageHashMap["sender"] = senderId
        messageHashMap["message"] = message
        messageHashMap["receiver"] = recieverId
        messageHashMap["isseen"] = false
        messageHashMap["url"] = ""
        messageHashMap["messageId"] = messageKey

        reference.child("Chats")
            .child(messageKey!!)
            .setValue(messageHashMap).addOnCompleteListener {task ->
                if(task.isSuccessful){
                    /**for sender**/
                    val chatListReference = FirebaseDatabase.getInstance().reference
                        .child("ChatList")
                        .child(firebaseUser!!.uid)
                        .child(chatUserId!!)
                    chatListReference.addListenerForSingleValueEvent(object: ValueEventListener{
                        override fun onDataChange(p0: DataSnapshot) {
                           if(!p0.exists()){
                               chatListReference.child("id").setValue(chatUserId)
                           }

                            /***for receiver**/
                            val chatListReceiverRef = FirebaseDatabase.getInstance().reference
                                .child("ChatList")
                                .child(chatUserId!!)
                                .child(firebaseUser!!.uid)
                            chatListReceiverRef.child("id").setValue(firebaseUser!!.uid)
                        }

                        override fun onCancelled(p0: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })



                    val reference = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

                    //implement push notifications
                }
            }
    }

    /**12B retrieve messages of both the receiver and sender fom db***/
    fun retrieveMessages(senderId: String, receiverId: String?, receiverImage: String?){
        chatList = ArrayList()

        val reference = FirebaseDatabase.getInstance().reference.child("Chats")

        reference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                (chatList as ArrayList<Chats>).clear()

                for(snapshot in p0.children){
                    val chat = snapshot.getValue(Chats::class.java)

                    if(chat!!.receiver.equals(senderId) && chat.sender.equals(receiverId)
                        || chat.receiver.equals(receiverId) && chat.sender.equals(senderId) ){

                        (chatList as ArrayList<Chats>).add(chat)
                    }

                    chatAdapter = ChatsAdapter(this@MessagingActivity,(chatList as ArrayList<Chats>), receiverImage!! )
                    recyclerView.adapter = chatAdapter
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==RequestCode && resultCode == RESULT_OK && data!=null && data?.data != null){

            var loadingBar = ProgressDialog(this)
            loadingBar.setMessage("Loading...")
            loadingBar.show()

            /**save images to firebase**/
            val fileUri = data.data
            val storageReference = FirebaseStorage.getInstance().reference.child("Chat Images")
            val ref = FirebaseDatabase.getInstance().reference
            val messageId = ref.push().key
            val filePath = storageReference.child("$messageId.jpg")

            val uploadTask: StorageTask<*>
            uploadTask = filePath!!.putFile(fileUri!!)
            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if(!task.isSuccessful){
                    task.exception?.let{
                        throw it
                    }
                }

                return@Continuation filePath.downloadUrl
            }).addOnCompleteListener {task->
                if(task.isSuccessful) {
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    val messageHashMap = HashMap<String, Any?>()
                    messageHashMap["sender"] = firebaseUser!!.uid
                    messageHashMap["message"] = "sent you an image."
                    messageHashMap["receiver"] = chatUserId
                    messageHashMap["isseen"] = false
                    messageHashMap["url"] = url
                    messageHashMap["messageId"] = messageId

                    ref.child("Chats").child(messageId!!).setValue(messageHashMap)
                }
            }
        }
    }
}