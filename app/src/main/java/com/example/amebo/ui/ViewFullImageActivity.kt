package com.example.amebo.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.amebo.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_view_full_image.*

class ViewFullImageActivity : AppCompatActivity() {

    private var imageUrl:String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_full_image)

        imageUrl = intent.getStringExtra("url")

        Picasso.get().load(imageUrl).into(view_image)
    }
}