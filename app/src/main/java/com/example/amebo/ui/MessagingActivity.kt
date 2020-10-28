package com.example.amebo.ui

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.amebo.R
import com.example.amebo.adapters.ChatsAdapter
import com.example.amebo.model.Chats
import com.example.amebo.model.Users
import com.example.amebo.network.ApiService
import com.example.amebo.notifications.*
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_messaging.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MessagingActivity : AppCompatActivity() {

    var chatUserId: String? = ""
    var firebaseUser: FirebaseUser? = null
    val RequestCode = 1
    var chatAdapter: ChatsAdapter? = null
    var chatList:List<Chats>? = null
    lateinit var recyclerView: RecyclerView
    var reference: DatabaseReference? = null
    var notify: Boolean = false
    var apiService : ApiService? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messaging)

        /**add back button**/
        setSupportActionBar(messaging_toolbar)
        supportActionBar!!.title=""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        /**hide status bar***/
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()

        messaging_toolbar.setNavigationOnClickListener {
            finish()
        }

        apiService = Client.Client.getClient("https://fcm.googleapis.com/")!!.create(ApiService::class.java)

        chatUserId = intent.getStringExtra("chat_with")

        firebaseUser = FirebaseAuth.getInstance().currentUser

        /***12C setup recylerview**/
        recyclerView = messaging_rv
        recyclerView.setHasFixedSize(true)
        var linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager


        /***11b display receiver details**/
        reference = FirebaseDatabase.getInstance().reference.child("Users").child(chatUserId!!)
        reference!!.addValueEventListener(object: ValueEventListener{
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
            notify = true
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
            notify = true
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "select Image"),RequestCode )
        }

        /**14 seen func**/
        seenMessage(chatUserId!!)
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

                }
            }

        /**implement push notifications**/
        val ref = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                val user= p0.getValue(Users::class.java)
                if(notify){
                    sendNotification(recieverId, user!!.userName, message)
                }
                notify = false
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
        /**17 send notification func**/
    private fun sendNotification(receiverId: String?, userName: String, message: String) {
        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
            val query = ref.orderByKey().equalTo(receiverId)

            query.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    for (snapshot in p0.children) {
                        val token: Token? = snapshot.getValue(Token::class.java)

                        val data = Data(
                            firebaseUser!!.uid,
                            R.mipmap.ic_launcher_round,
                            "$userName: $message",
                            "New Message",
                            chatUserId!!
                        )
                        val sender = Sender(data, token!!.token)

                        apiService!!.sendNotification(sender)
                            .enqueue(object : Callback<MyResponse> {
                                override fun onResponse(
                                    call: Call<MyResponse>,
                                    response: Response<MyResponse>
                                ) {
                                    if (response.code() == 200) {
                                        if (response.body()!!.success != 1) {
                                            Toast.makeText(this@MessagingActivity, "Failed", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }

                                override fun onFailure(call: Call<MyResponse>, t: Throwable) {
                                    return
                                }
                            })
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
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

    /**14 seen message func**/
    var seenListener: ValueEventListener? = null
    private fun seenMessage(userId:String){
        val ref = FirebaseDatabase.getInstance().reference.child("Chats")

        seenListener = ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                for(snapshot in p0.children){
                    val chat = snapshot.getValue(Chats::class.java)
                    if(chat!!.receiver.equals(firebaseUser!!.uid) && chat!!.sender.equals(userId)){

                        val hashMap = HashMap<String, Any>()
                        hashMap["isseen"] = true
                        snapshot.ref.updateChildren(hashMap)
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun onPause() {
        super.onPause()
        reference!!.removeEventListener(seenListener!!)

        /**hide status bar***/
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
    }

    override fun onResume() {
        super.onResume()
        /**hide status bar***/
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
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
                        .addOnCompleteListener { task->
                            if(task.isSuccessful){

                                loadingBar.dismiss()

                                /**implement push notifications**/
                                val reference = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
                                reference.addValueEventListener(object: ValueEventListener{
                                    override fun onDataChange(p0: DataSnapshot) {
                                        val user= p0.getValue(Users::class.java)
                                        if(notify){
                                            sendNotification(chatUserId, user!!.userName, "sent you an image.")
                                        }
                                        notify = false
                                    }

                                    override fun onCancelled(p0: DatabaseError) {
                                        TODO("Not yet implemented")
                                    }
                                })
                            }

                        }


                }
            }
        }
    }
}