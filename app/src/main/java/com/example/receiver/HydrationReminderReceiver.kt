package com.example.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity

class HydrationReminderReceiver : BroadcastReceiver() {
    companion object {
        const val CHANNEL_ID = "eissery_reminders_channel"
        const val NOTIFICATION_ID = 1001

        val friendlyMessages = listOf(
            "Hey friend! Just a gentle nudge from your Eisser Daily pal: take a nice sip of water to keep your energy high today! 💧",
            "Eisser hydration check! Your brain needs some H2O to power through your classes. Keep shining! ✨",
            "Sip alert! A quick glass of water makes a huge difference. You've got this, let's log some mLs! 🥤",
            "Staying hydrated is your secret student superpower, buddy. Treat yourself to a cold refreshing glass! 🚀",
            "Time to hydrate, friend! Your daily hydration goal is waiting. You're doing amazing! 🌱"
        )
    }

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create Channel for Oreo+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Eisser Daily Hydration Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Friendly student reminders to drink water and earn rewards."
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Pick a random supportive message
        val randomMessage = friendlyMessages.random()

        // Create intent to launch app on click
        val clickIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            clickIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build notification
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // System info icon as safe default
            .setContentTitle("Friendly Hydration Check! 💧")
            .setContentText(randomMessage)
            .setStyle(NotificationCompat.BigTextStyle().bigText(randomMessage))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
