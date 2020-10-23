package com.example.amebo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.amebo.R
import com.example.amebo.model.Users
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.main_app_bar_layout.*
import kotlinx.android.synthetic.main.user_search_item_layout.view.*
/**8 adapter for search fragment**/
class UserAdapter(
    private val context: Context,
    private val mUsers :List<Users>,
    private val isChatChecked:Boolean
    ) : RecyclerView.Adapter<UserAdapter.ViewHolder>(){

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
       // Picasso.get().load(user.getPROFILE()).placeholder(R.drawable.ic_profile).into(holder.profileImageView)
    }

    override fun getItemCount(): Int = mUsers.size
}