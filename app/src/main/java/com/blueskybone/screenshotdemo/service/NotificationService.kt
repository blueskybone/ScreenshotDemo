package com.blueskybone.screenshotdemo.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.blueskybone.screenshotdemo.APP
import com.blueskybone.screenshotdemo.Home
import com.blueskybone.screenshotdemo.R
import com.blueskybone.screenshotdemo.activity.ScreenshotActivity
import com.blueskybone.screenshotdemo.util.stringRes

/**
 *   Created by blueskybone
 *   Date: 2024/4/1
 */

class NotificationService : Service() {
    companion object {
        private const val TAG = "NotificationService"

        private const val FOREGROUND_SERVICE_ID = 7594
        private const val CHANNEL_FORE_ID = "1094"
        private const val CHANNEL_FORE_NAME = "screenshot_fore_service"

        private var notification: Notification? = null
        var instance: NotificationService? = null
    }

    override fun onCreate() {
        instance = this
        createNotification()
        super.onCreate()
    }

    override fun onDestroy() {
        APP.releaseMutexService()
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        foreground()
        val scIntent = Intent(this, ScreenshotActivity::class.java)
        scIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        this.startActivity(scIntent)
        return super.onStartCommand(intent, flags, startId)
    }

    private fun createNotification() {
        val channelFore = NotificationChannel(
            CHANNEL_FORE_ID,
            CHANNEL_FORE_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channelFore)

        val intent = Intent(this, Home::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        notification = Notification.Builder(this, CHANNEL_FORE_ID)
            .setChannelId(CHANNEL_FORE_ID)
            .setSmallIcon(R.drawable.icon)
            .setContentIntent(pendingIntent)
            .setContentTitle(stringRes(R.string.app_name))
            .setContentText(stringRes(R.string.notification_content))
            .setAutoCancel(true)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")

    }

    private fun foreground() {
        println("$TAG: foreground()")
        startForeground(FOREGROUND_SERVICE_ID, notification)
    }
}