package com.blueskybone.screenshotdemo.service

import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.Surface
import androidx.core.app.NotificationCompat
import com.blueskybone.screenshotdemo.Home
import com.blueskybone.screenshotdemo.R
import com.blueskybone.screenshotdemo.util.convertImageToBitmap
import com.blueskybone.screenshotdemo.util.getDensityDpi
import com.blueskybone.screenshotdemo.util.getFormatDate
import com.blueskybone.screenshotdemo.util.getRealScreenSize
import com.blueskybone.screenshotdemo.util.saveBitmapToGallery
import com.blueskybone.screenshotdemo.util.stringRes

class ScreenshotService : Service() {

    private val handler = Handler(Looper.getMainLooper())
    private var inactivityRunnable: Runnable? = null

    //service kill self timer, each time the service invoked, timer will reset. after 1min request for capturePermission again.
    private val inactivityTimeout: Long = 1 * 60 * 1000
    companion object {
        private const val FOREGROUND_SERVICE_ID = 2375
        private const val CHANNEL_FORE_ID = "1842"
        private const val CHANNEL_FORE_NAME = "screenshot_fore_service"

        private var screenWidth = 0
        private var screenHeight = 0
        private var screenDensityDpi = 0

        private var mutex: Boolean = false
    }

    private var mediaProjection: MediaProjection? = null
    private var mediaProjectionManager: MediaProjectionManager? = null
    private var notification: Notification? = null

    private var imageReader: ImageReader? = null
    private var surface: Surface? = null
    private var virtualDisplay: VirtualDisplay? = null


    private fun setScreenSize() {
        screenDensityDpi = getDensityDpi(this)
        val point = getRealScreenSize(this)
        screenWidth = point.x
        screenHeight = point.y
    }


    override fun onCreate() {
        super.onCreate()
        setScreenSize()
        createNotification()
        startInactivityTimer()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (mutex) {
            return START_NOT_STICKY
        }
        mutex = true

        resetInactivityTimer()

        if (CapturePermission.intent == null) {
            val acquireIntent = Intent(this, AcquireCapturePermission::class.java)
            acquireIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(acquireIntent)
            return super.onStartCommand(intent, flags, startId)
        }

        intent.apply {
            val result = intent?.extras?.getBoolean("setPermission")
            if (result == true) {
                foreground()
                (getSystemService(Context.MEDIA_PROJECTION_SERVICE) as? MediaProjectionManager)?.apply {
                    mediaProjection = this.getMediaProjection(
                        Activity.RESULT_OK,
                        (CapturePermission.intent!!.clone() as Intent)
                    )
                    mediaProjection?.registerCallback(MyCallBack(), null)
                }
                setupImageReader()
                createVirtualDisplay()
            }
        }

        try {
            Thread.sleep(1000L)
            imageReader!!.acquireLatestImage().use { image ->
                if (image != null) {
                    val bitmap = convertImageToBitmap(image, Bitmap.Config.ARGB_8888)
                    Thread {
                        val filename = "screenshot_${getFormatDate()}"
                        saveBitmapToGallery(this, bitmap, filename)
                        bitmap.recycle()
                    }.start()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            mutex = false
        }
        mutex = false
        return START_NOT_STICKY
    }

    private fun setupImageReader() {
        imageReader = ImageReader.newInstance(
            screenWidth,
            screenHeight, PixelFormat.RGBA_8888, 2
        )
        surface = imageReader!!.surface
    }

    private fun startInactivityTimer() {
        inactivityRunnable = Runnable {
            stopSelf()
        }
        handler.postDelayed(inactivityRunnable!!, inactivityTimeout)
    }

    private fun resetInactivityTimer() {
        handler.removeCallbacks(inactivityRunnable!!)
        handler.postDelayed(inactivityRunnable!!, inactivityTimeout)
    }

    private fun createVirtualDisplay() {
        virtualDisplay = mediaProjection!!.createVirtualDisplay(
            "ScreenShot",
            screenWidth,
            screenHeight,
            screenDensityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            //DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY or DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
            surface,
            null,
            null
        )
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

        val snoozeIntent = Intent(this, Home::class.java).apply {
            action = "KILL_SERVICE"
            putExtra(NotificationCompat.EXTRA_NOTIFICATION_ID, 0)
        }
        val snoozePendingIntent: PendingIntent =
            PendingIntent.getBroadcast(this, 0, snoozeIntent, PendingIntent.FLAG_IMMUTABLE)

        notification = Notification.Builder(this, CHANNEL_FORE_ID)
            .setChannelId(CHANNEL_FORE_ID)
            .setSmallIcon(R.drawable.icon)
            .setContentIntent(snoozePendingIntent)
            .setContentTitle(stringRes(R.string.app_name))
            .setContentText(stringRes(R.string.notification_content))
            .setAutoCancel(true)
            .build()
    }

    private fun foreground() {
        startForeground(FOREGROUND_SERVICE_ID, notification)
    }

    private fun background() {
        stopForeground(STOP_FOREGROUND_DETACH)
    }

    override fun onDestroy() {
        super.onDestroy()
        background()
        mediaProjection = null
        mediaProjectionManager = null
        imageReader = null
        surface?.release()
        virtualDisplay?.release()
        CapturePermission.intent = null
        handler.removeCallbacks(inactivityRunnable!!)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    inner class MyCallBack : MediaProjection.Callback() {
        override fun onStop() {
            super.onStop()
        }
    }

}