package com.example.amebo.ui.fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.example.amebo.R
import com.example.amebo.model.Users
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_settings.view.*


class SettingsFragment : Fragment() {

    var userReference:DatabaseReference? = null
    var firebaseUser:FirebaseUser? = null
    private  val RequestCode = 1
    private var imageUri: Uri? = null
    private var storageRef: StorageReference? = null
    private var coverChecker: String? = null
    private var socialChecker: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        userReference = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        storageRef = FirebaseStorage.getInstance().reference.child("user Images")

        /**9 retrieve data from database for user settings***/
        userReference!!.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
               if(p0.exists()){
                   val user: Users? =p0.getValue(Users::class.java)

                   if(context != null){
                       view.username_settings.text = user?.userName
                       Picasso.get().load(user?.profile).placeholder(R.drawable.user_icon).into(view.profile_image_settings)
                       //Picasso.get().load(user?.cover).placeholder(R.drawable.cover).into(view.cover_image_settings)
                   }


               }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        /***update profile pic**/
        view.profile_image_settings.setOnClickListener {
            pickImage()
        }

//        /***update cover pic**/
//        view.cover_image_settings.setOnClickListener {
//            coverChecker = "cover"
//            pickImage()
//        }

        /***set social links**/
        view.set_fb.setOnClickListener {
            socialChecker ="fb"
            setSocialLink()
        }
        view.set_ig.setOnClickListener {
            socialChecker = "ig"
            setSocialLink()
        }
        view.set_web.setOnClickListener {
            socialChecker = "web"
            setSocialLink()
        }

        return view
    }
        /***10 get image assets from user gallery**/
        private fun pickImage(){
            val intent = Intent()
            intent.type = "image/*"
            intent.action =Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, RequestCode)
        }
    /**upload image to firebase**/
    private fun uploadImage(){
        val progressBar = ProgressDialog(context)
        progressBar.setMessage("Loading...")
        progressBar.show()

        if(imageUri != null){
            val fileRef = storageRef?.child(System.currentTimeMillis().toString() + ".jpg")

            var uploadTask:StorageTask<*>
            uploadTask = fileRef!!.putFile(imageUri!!)
            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if(!task.isSuccessful){
                    task.exception?.let{
                        throw it
                    }
                }

                return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener {task->
                if(task.isSuccessful){
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    if(coverChecker == "cover"){
                        val coverMap = HashMap<String, Any>()
                        coverMap["cover"] = url
                        userReference?.updateChildren(coverMap)
                        coverChecker = null
                    }
                    else{
                        val profileMap = HashMap<String, Any>()
                        profileMap["profile"]=url
                        userReference?.updateChildren(profileMap)
                    }
                    progressBar.dismiss()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==RequestCode && resultCode == Activity.RESULT_OK && data?.data != null){
            imageUri = data.data

            uploadImage()
        }
    }

    private fun setSocialLink(){
        val builder: AlertDialog.Builder = AlertDialog.Builder(context, R.style.Theme_AppCompat_DayNight_Dialog)

        if(socialChecker == "web"){
            builder.setTitle("Write URL:")
        }
        else{
            builder.setTitle("Write username:")
        }
        val editText = EditText(context)

        if(socialChecker == "web"){
            editText.hint = "eg www.amebo.com"
        }
        else{
            editText.hint = "eg hyppi54"
        }

        builder.setView(editText)

        builder.setPositiveButton("Create", DialogInterface.OnClickListener { dialog, which ->
            val str = editText.text.toString()
            if(str.isNotEmpty()){
                saveSocialLink(str)
            }
        })

        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
            dialog.cancel()
        })

        builder.show()
    }

    private fun saveSocialLink(str: String){
        val socialMap = HashMap<String, Any>()

        when(socialChecker){
            "fb" ->{
                socialMap["facebook"]="https://m.facebook.com/$str"
            }
            "ig" ->{
                socialMap["instagram"]="https:/m.instagram.com/$str"
            }
            "web" ->{
                socialMap["website"]="https://$str"
            }
        }

        userReference?.updateChildren(socialMap)?.addOnCompleteListener {task->

            if(task.isSuccessful){
                Toast.makeText(requireContext(), "updated successfully", Toast.LENGTH_SHORT).show()
            }
        }

    }
}