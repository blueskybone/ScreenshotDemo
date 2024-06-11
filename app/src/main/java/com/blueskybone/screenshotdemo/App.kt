package com.blueskybone.screenshotdemo

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.view.Gravity
import com.blueskybone.screenshotdemo.activity.DynamicPermissionActivity
import com.blueskybone.screenshotdemo.service.NotificationService
import com.hjq.toast.Toaster
import com.hjq.toast.style.BlackToastStyle

/**
 *   Created by blueskybone
 *   Date: 2024/4/1
 */
lateinit var APP: App

class App : Application() {
    companion object {
        var screenshotPermission: Intent? = null
        var mediaProjectionManager: MediaProjectionManager? = null

        private var mutexActivity = true
        private var mutexService = true
    }

    init {
        Toaster.init(this)
        APP = this
    }

    override fun onCreate() {

        Toaster.setStyle(BlackToastStyle())
        Toaster.setGravity(Gravity.TOP, 0, 180)

        super.onCreate()
    }

    fun getScreenshotPermission(): Intent? {
        return screenshotPermission
    }

    fun setScreenshotPermission(permissionIntent: Intent?) {
        if (permissionIntent != null) {
            screenshotPermission = permissionIntent
        }
    }

    fun setMediaProjectionManager(mpm: MediaProjectionManager) {
        mediaProjectionManager = mpm
    }

    fun createMediaProjection(): MediaProjection? {
        return mediaProjectionManager!!.getMediaProjection(
            Activity.RESULT_OK,
            (screenshotPermission!!.clone() as Intent)
        )
    }

    // lock service and screenshot activity to avoid crash caused
    // by executing several tasks in a very short time.
    fun startScreenshot() {
        if (mutexActivity && mutexService) {
            mutexActivity = false
            mutexService = false
            val intent = Intent(this, NotificationService::class.java)
            startForegroundService(intent)
        }
    }

    fun releaseMutexService() {
        mutexService = true
    }

    fun releaseMutexActivity() {
        mutexActivity = true
    }

    fun stopScreenTaskService() {
        val intent = Intent(this, NotificationService::class.java)
        stopService(intent)
    }

    fun startScreenTask(context: Context) {
        //Android14开始，不能通过保存ScreenshotPermission的方法反复截屏，
        //只能每次截屏获取一次动态权限。
        if (Build.VERSION.SDK_INT < 34 && APP.getScreenshotPermission() != null) {
            val intent = Intent(context, NotificationService::class.java)
            context.startForegroundService(intent)
        } else {
            val acquireIntent = Intent(context, DynamicPermissionActivity::class.java)
            acquireIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(acquireIntent)
        }
    }
}