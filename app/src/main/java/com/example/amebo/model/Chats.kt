package com.example.amebo.model

data class Chats (
    var sender: String ="",
    var message:String ="",
    var receiver:String ="",
    var isseen:Boolean =false,
    var url:String ="",
    var messageId:String=""

)