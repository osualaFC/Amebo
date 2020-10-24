package com.example.amebo.network

import com.example.amebo.notifications.Sender
import com.example.amebo.notifications.MyResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {

    @Headers(
        "Content-Type:applicaation/json",
        "Authorization:key=AAAAOxm4xsY:APA91bHLHVQtz4Ibn7qmEo2g1h9_q5BdQJZeW2HuSQDlbzjiFowMktwVywxYRNMf5x1L8nAT4EMXVw7e7kgxJw8sgWHycFJFm5QWRcSGxgHIhY9QBbwiV4uqEJ4f7RDFu_z3R6kY2zft"
    )

    @POST("fcm/send")
    fun sendNotification( @Body sender: Sender): Call<MyResponse>



}