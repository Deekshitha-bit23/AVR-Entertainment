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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

class LocalNotificationService(private val context: Context) {
    
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
    companion object {
        const val CHANNEL_ID = "expense_notifications"
        const val CHANNEL_NAME = "Expense Notifications"
        const val CHANNEL_DESCRIPTION = "Notifications for expense approvals and updates"
    }
    
    init {
        createNotificationChannel()
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    fun sendExpenseSubmittedNotification(
        expenseAmount: String,
        projectName: String,
        submitterName: String
    ) {
        val title = "New Expense Submitted"
        val message = "₹$expenseAmount expense for $projectName by $submitterName needs approval"
        
        showNotification(
            title = title,
            message = message,
            notificationId = Random.nextInt()
        )
    }
    
    fun sendExpenseApprovedNotification(
        expenseAmount: String,
        projectName: String,
        approverName: String
    ) {
        val title = "Expense Approved"
        val message = "Your ₹$expenseAmount expense for $projectName has been approved by $approverName"
        
        showNotification(
            title = title,
            message = message,
            notificationId = Random.nextInt()
        )
    }
    
    fun sendExpenseRejectedNotification(
        expenseAmount: String,
        projectName: String,
        approverName: String
    ) {
        val title = "Expense Rejected"
        val message = "Your ₹$expenseAmount expense for $projectName has been rejected by $approverName"
        
        showNotification(
            title = title,
            message = message,
            notificationId = Random.nextInt()
        )
    }
    
    // NEW NOTIFICATION METHODS FOR ENHANCED SYSTEM

    fun sendBudgetAddedNotification(
        amount: String,
        department: String,
        projectName: String
    ) {
        val title = "Budget Added"
        val message = "₹$amount has been added to $department budget for $projectName"
        
        showNotification(
            title = title,
            message = message,
            notificationId = Random.nextInt()
        )
    }

    fun sendBudgetDeductedNotification(
        amount: String,
        department: String,
        projectName: String
    ) {
        val title = "Budget Deducted"
        val message = "₹$amount has been deducted from $department budget for $projectName"
        
        showNotification(
            title = title,
            message = message,
            notificationId = Random.nextInt()
        )
    }

    fun sendBudgetExceededNotification(
        scopeName: String,
        exceededAmount: String,
        currentSpent: String,
        budgetLimit: String,
        isProjectLevel: Boolean = false
    ) {
        val title = "⚠️ Budget Exceeded"
        val scope = if (isProjectLevel) "Project" else "Department"
        val message = "$scope $scopeName budget exceeded by ₹$exceededAmount. Current: ₹$currentSpent | Limit: ₹$budgetLimit"
        
        showNotification(
            title = title,
            message = message,
            notificationId = Random.nextInt()
        )
    }

    fun sendPendingApprovalReminderNotification(
        pendingCount: Int,
        totalAmount: String,
        projectName: String
    ) {
        val title = "Pending Approvals Reminder"
        val message = "$pendingCount expenses (₹$totalAmount) awaiting approval for $projectName"
        
        showNotification(
            title = title,
            message = message,
            notificationId = Random.nextInt()
        )
    }

    // Generic method for any notification type
    fun sendCustomNotification(
        title: String,
        message: String
    ) {
        showNotification(
            title = title,
            message = message,
            notificationId = Random.nextInt()
        )
    }
    
    private fun showNotification(
        title: String,
        message: String,
        notificationId: Int
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()
        
        notificationManager.notify(notificationId, notification)
        
        android.util.Log.d("LocalNotification", "Notification sent: $title - $message")
    }
    
    // Simulate delayed push notification (for testing)
    fun sendDelayedNotification(
        title: String,
        message: String,
        delayMs: Long = 3000
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            kotlinx.coroutines.delay(delayMs)
            showNotification(
                title = title,
                message = message,
                notificationId = Random.nextInt()
            )
        }
    }
} 