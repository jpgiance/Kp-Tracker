package com.autonomy_lab.kptracker.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationCompat
import com.autonomy_lab.kptracker.MainActivity
import com.autonomy_lab.kptracker.R
import javax.inject.Inject

class NotificationProvider @Inject constructor(
    private val context: Context
) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel(){

        val channel = NotificationChannel(
            SERVICE_ACTIVE_CHANNEL_ID,
            "Kp Index Update",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = "Used for notify the user that the Kp index is above the threshold"

        notificationManager.createNotificationChannel(channel)
    }




    fun sendNotification(title: String, message: String, icon: Int = R.drawable.planet_icon) {

        val activityIntent = Intent(context, MainActivity::class.java)
        val activityPendingIntent = PendingIntent.getActivity(context, 1, activityIntent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(context, SERVICE_ACTIVE_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(icon) // Replace with your icon
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(activityPendingIntent)
            .setAutoCancel(true) // Dismiss the notification when tapped
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)


    }

    fun isNotificationActive(): Boolean{
        val activeNotifications: Array<StatusBarNotification> = notificationManager.activeNotifications

        return activeNotifications.any { it.id == NOTIFICATION_ID }
    }

    companion object {
        const val SERVICE_ACTIVE_CHANNEL_ID = "service_active_channel"
        const val NOTIFICATION_ID = 222
    }
}