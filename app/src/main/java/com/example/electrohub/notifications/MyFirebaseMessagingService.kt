package com.example.electrohub.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.electrohub.MainActivity
import com.example.electrohub.R
import com.example.electrohub.repositories.UsersRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MyFirebaseMessagingService :
    FirebaseMessagingService() {

    private val serviceScope =
        CoroutineScope(
            SupervisorJob() + Dispatchers.IO
        )

    private val userRepository =
        UsersRepository()


    // FCM TOKEN
    override fun onNewToken(
        token: String
    ) {

        super.onNewToken(token)

        Log.d(
            "FCM_TOKEN",
            "New FCM token received"
        )

        serviceScope.launch {

            val success =
                userRepository.updateFcmToken(
                    token
                )

            if (!success) {

                Log.e(
                    "FCM_TOKEN",
                    "Failed to update FCM token"
                )
            }
        }
    }


    // RECEIVE MESSAGE
    override fun onMessageReceived(
        remoteMessage: RemoteMessage
    ) {

        super.onMessageReceived(
            remoteMessage
        )

        val chatId =
            remoteMessage.data["chatId"]


        val title =
            remoteMessage.notification?.title
                ?: remoteMessage.data["title"]
                ?: "New Message"


        val message =
            remoteMessage.notification?.body
                ?: remoteMessage.data["message"]
                ?: "You received a new message"


        showNotification(
            title = title,
            message = message,
            chatId = chatId
        )
    }


    // SHOW NOTIFICATION
    private fun showNotification(
        title: String,
        message: String,
        chatId: String?
    ) {

        val channelId =
            "chat_notifications"


        val intent =
            Intent(
                this,
                MainActivity::class.java
            ).apply {

                flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP

                if (!chatId.isNullOrEmpty()) {

                    putExtra(
                        "chatId",
                        chatId
                    )
                }
            }


        val pendingIntent =
            PendingIntent.getActivity(
                this,
                chatId?.hashCode()
                    ?: System.currentTimeMillis().toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )


        val builder =
            NotificationCompat.Builder(
                this,
                channelId
            )
                .setSmallIcon(
                    R.drawable.electrohub_logo
                )
                .setContentTitle(
                    title
                )
                .setContentText(
                    message
                )
                .setAutoCancel(
                    true
                )
                .setContentIntent(
                    pendingIntent
                )
                .setPriority(
                    NotificationCompat.PRIORITY_HIGH
                )


        val notificationManager =
            getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager


        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {

            val channel =
                NotificationChannel(
                    channelId,
                    "Chat Messages",
                    NotificationManager.IMPORTANCE_HIGH
                )

            notificationManager
                .createNotificationChannel(
                    channel
                )
        }


        notificationManager.notify(
            chatId?.hashCode()
                ?: System.currentTimeMillis().toInt(),
            builder.build()
        )
    }


    // CLEANUP
    override fun onDestroy() {

        serviceScope.coroutineContext.cancel()

        super.onDestroy()
    }
}