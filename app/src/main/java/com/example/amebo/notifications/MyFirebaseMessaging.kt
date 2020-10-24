package com.example.amebo.notifications

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.amebo.MessagingActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessaging: FirebaseMessagingService() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val sent = remoteMessage.data["sent"]
        val user = remoteMessage.data["user"]
        val sharedPrefs = getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        val currentOnlineUser = sharedPrefs.getString("currentUser", "none")

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if(firebaseUser!=null && sent == firebaseUser.uid){
            if(currentOnlineUser != null){
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    sendOreoNotification(remoteMessage)
                }
                else{
                    sendNotification(remoteMessage)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendOreoNotification(remoteMessage: RemoteMessage){

        val user = remoteMessage.data["user"]
        val icon = remoteMessage.data["icon"]
        val title = remoteMessage.data["title"]
        val body = remoteMessage.data["body"]

        val notification = remoteMessage.notification
        val j = user!!.replace("[\\D]".toRegex(), "").toInt()
        /***send user ot messaging activity when he clicks on the notification tab**/
        val intent = Intent(this, MessagingActivity::class.java)

        val bundle =Bundle()
        bundle.putString("userId", user)
        intent.putExtras(bundle)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT)

        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val oreoNotification = OreoNotification(this)

        val builder: Notification.Builder = oreoNotification.getOreoNotification(title, body, pendingIntent, defaultSound, icon)

        var i = 0
        if(j > 0){
            i = j
        }
        oreoNotification.getManager!!.notify(i, builder.build())

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendNotification(remoteMessage: RemoteMessage){

        val user = remoteMessage.data["user"]
        val icon = remoteMessage.data["icon"]
        val title = remoteMessage.data["title"]
        val body = remoteMessage.data["body"]

        val notification = remoteMessage.notification
        val j = user!!.replace("[\\D]".toRegex(), "").toInt()
        /***send user ot messaging activity when he clicks on the notification tab**/
        val intent = Intent(this, MessagingActivity::class.java)

        val bundle =Bundle()
        bundle.putString("userId", user)
        intent.putExtras(bundle)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT)

        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder: NotificationCompat.Builder? = NotificationCompat.Builder(this)
            .setSmallIcon(icon!!.toInt())
            .setContentTitle(title)
            .setContentText(body)
            .setSound(defaultSound)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notify = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        var i = 0
        if(j > 0){
            i = j
        }
        notify.notify(i, builder?.build())

    }
}