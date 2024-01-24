package com.example.snowfallcountdown

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

const val NOTIFICATION_CHANNEL_ID = "notification_channel_1"
const val NOTIFICATION_CHANNEL_NAME = "message_channel_1"

class NotificationApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val notificationChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }
}