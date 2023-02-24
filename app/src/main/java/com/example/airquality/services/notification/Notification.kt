package com.example.airquality.services.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.airquality.MainActivity
import com.example.airquality.R

class Notification(var context: Context, var title: String, var message: String) {
    private val channelId: String = "HelloNotification"
    private val channelName: String = "NotificationMessage"
    private val notificationManager =
        context.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var notificationBuilder: NotificationCompat.Builder

    fun notification(id: Int) {
        notificationChannel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(notificationChannel)

        val intent = Intent(context, MainActivity::class.java)

        PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE)

        notificationBuilder = NotificationCompat.Builder(context, channelId)

        notificationBuilder
            .setSmallIcon(R.drawable.alarm)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)

        notificationManager.notify(id, notificationBuilder.build())
    }

    fun cancel(id: Int) {
        notificationManager.cancel(id)
    }
}