package com.deeksha.avrentertainment.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.deeksha.avrentertainment.MainActivity
import com.deeksha.avrentertainment.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class FCMService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Handle FCM message here
        remoteMessage.notification?.let { notification ->
            sendNotification(
                title = notification.title ?: "AVR Entertainment",
                body = notification.body ?: "You have a new notification",
                data = remoteMessage.data
            )
        }

        // Handle data payload
        if (remoteMessage.data.isNotEmpty()) {
            handleDataPayload(remoteMessage.data)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        
        // Send token to server
        sendRegistrationTokenToServer(token)
    }

    private fun sendNotification(title: String, body: String, data: Map<String, String>) {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            // Add extra data for navigation
            data["expenseId"]?.let { putExtra("expenseId", it) }
            data["projectId"]?.let { putExtra("projectId", it) }
            data["notificationType"]?.let { putExtra("notificationType", it) }
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "expense_notifications"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for Android 8.0 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Expense Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for expense approvals and updates"
            }
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(Random.nextInt(), notificationBuilder.build())
    }

    private fun handleDataPayload(data: Map<String, String>) {
        // Handle different types of notifications
        when (data["type"]) {
            "EXPENSE_SUBMITTED" -> {
                // Handle expense submitted notification
            }
            "EXPENSE_APPROVED" -> {
                // Handle expense approved notification
            }
            "EXPENSE_REJECTED" -> {
                // Handle expense rejected notification
            }
            "PROJECT_CREATED" -> {
                // Handle project created notification
            }
            "PROJECT_ASSIGNMENT" -> {
                // Handle project assignment notification
                val projectName = data["projectName"] ?: "Unknown Project"
                val role = data["role"] ?: "Unknown Role"
                
                // Send local notification for project assignment
                val notificationService = LocalNotificationService(this)
                notificationService.sendProjectAssignmentNotification(projectName, role)
            }
        }
    }

    private fun sendRegistrationTokenToServer(token: String) {
        // Store FCM token in Firestore
        android.util.Log.d("FCM Token", "New token: $token")
        
        // Store token in SharedPreferences for immediate access
        val sharedPrefs = getSharedPreferences("FCM_TOKEN", Context.MODE_PRIVATE)
        sharedPrefs.edit().putString("token", token).apply()
        
        // TODO: Store in Firestore under user's document when user is logged in
        // This will be handled in the login flow
    }
} 