package com.example.rawan.radio

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat

import android.widget.RemoteViews


object NotificationUtilities{
    private val NOTIFICATION_ID = 1138
    private val NOTIFICATION_CHANNEL_ID = "reminder_notification_channel"
    lateinit var notificationBuilder: NotificationCompat.Builder
    @RequiresApi(Build.VERSION_CODES.O)
    fun notification(context: Context){

        val notificationManager= context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannel= NotificationChannel(NOTIFICATION_CHANNEL_ID,
                "channel Name",
                NotificationManager.IMPORTANCE_HIGH)

        notificationManager.createNotificationChannel(notificationChannel)
        notificationBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setStyle(NotificationCompat.BigTextStyle().bigText("klam"))
                .setSmallIcon(R.drawable.ic_radio_black_24dp)
                .setAutoCancel(false)
                .setShortcutId("radioId")
                .setOnlyAlertOnce(true)
                .setOngoing(true)
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }
    fun removeNotification(context: Context){
        val notificationManager= context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateNotifText(context: Context,steps :String){
        val notificationManager= context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationBuilder.setContentTitle(steps)
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())

    }

}