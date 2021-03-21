package com.koc.touchnotes.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.koc.touchnotes.R
import com.koc.touchnotes.util.Constants.CHANNEL_ID
import com.koc.touchnotes.util.Constants.CHANNEL_NAME
import com.koc.touchnotes.util.Constants.NOTIFICATION_ID

/**
Created by kelvin_clark on 3/21/2021 4:23 PM
 */
object NotificationUtil {

    fun createNotification(context: Context, pdfUri: Uri) {
        createNotificationChannel(context)

        val intent = Intent(Intent.ACTION_VIEW)
        intent.apply {
            type = "application/pdf"
            data = pdfUri
            flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        }

        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
        notification.apply {
            setContentTitle("PDF Generated")
            setContentText("PDF successfully created, click to view file.")
            setSmallIcon(R.drawable.ic_pdf)
            priority = NotificationCompat.PRIORITY_DEFAULT
            setContentIntent(pendingIntent)
            setAutoCancel(true)
        }

        NotificationManagerCompat.from(context)
            .notify(NOTIFICATION_ID, notification.build())
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }
    }
}