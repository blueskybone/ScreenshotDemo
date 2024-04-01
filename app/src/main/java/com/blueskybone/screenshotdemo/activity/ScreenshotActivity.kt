package com.blueskybone.screenshotdemo.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.view.Surface
import com.blueskybone.screenshotdemo.APP
import com.blueskybone.screenshotdemo.util.TimeUtils.getFormatDate
import com.blueskybone.screenshotdemo.util.convertImageToBitmap
import com.blueskybone.screenshotdemo.util.getDensityDpi
import com.blueskybone.screenshotdemo.util.getRealScreenSize
import com.blueskybone.screenshotdemo.util.saveBitmapToGallery
import com.hjq.toast.Toaster


/**
 *   Created by blueskybone
 *   Date: 2024/4/1
 */
class ScreenshotActivity : Activity() {

    companion object {
        private const val TAG = "ScreenshotActivity"

        private var mediaProjection: MediaProjection? = null
        private var imageReader: ImageReader? = null
        private var surface: Surface? = null
        private var virtualDisplay: VirtualDisplay? = null

        var screenWidth = 0
        var screenHeight = 0
        var screenDensityDpi = 0

        private const val delay: Long = 300

    }

    private fun setScreenSize() {
        screenDensityDpi = getDensityDpi(this)
        val point = getRealScreenSize(this)
        screenWidth = point.x
        screenHeight = point.y

        println("screenWidth: $screenWidth  screenHeight: $screenHeight")
    }

    override fun onStart() {
        super.onStart()
        //TODO: change function logic
        try {
            setScreenSize()
            if (prepareForScreenshot()) {
                val bitmap = takeOneScreenshot()
                if (bitmap != null) {
                    Thread {
                        val filename = "screenshot_${getFormatDate()}"
                        saveBitmapToGallery(this, bitmap, filename)
                    }.start()
                }
            }
            stop()
        } catch (e: Exception) {
            e.printStackTrace()
            stop()
        }
    }


    private fun takeOneScreenshot(): Bitmap? {
        Thread.sleep(delay)
        var bitmap: Bitmap? = null
        try {
            imageReader!!.acquireLatestImage().use { image ->
                if (image != null) {
                    bitmap = convertImageToBitmap(image, Bitmap.Config.ARGB_8888)
                } else {
                    println("get image null")
                    Toaster.show("error in acquire_image")
                }
            }
        } catch (e: Exception) {
            Toaster.show("error in acquire_latest_image")
            e.printStackTrace()
            return null
        }
        return bitmap
    }

    @SuppressLint("WrongConstant")
    private fun prepareForScreenshot(): Boolean {
        try {
            imageReader = ImageReader.newInstance(
                screenWidth,
                screenHeight, PixelFormat.RGBA_8888, 2
            )
            surface = imageReader!!.surface

            mediaProjection = APP.createMediaProjection()

            startVirtualDisplay()
        } catch (e: Exception) {
            Toaster.show("error in prepare_for_screenshot")
            e.printStackTrace()
            return false
        }
        return true
    }

    private fun startVirtualDisplay() {
        virtualDisplay?.release()
        virtualDisplay = mediaProjection!!.createVirtualDisplay(
            "ScreenShot",
            screenWidth,
            screenHeight,
            screenDensityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY
                    or DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
            surface,
            null,
            null
        )
    }

    private fun stop() {
        stopVirtualDisplay()
        stopSurface()
        imageReader = null
        mediaProjection?.stop()
        mediaProjection = null
        APP.stopScreenTaskService()
        finish()
    }

    private fun stopVirtualDisplay() {
        virtualDisplay?.release()
        virtualDisplay = null
    }

    private fun stopSurface() {
        surface?.release()
        surface = null
    }

    private fun releaseMutex() {
        APP.releaseMutexActivity()
    }

    override fun onDestroy() {
        stop()
        releaseMutex()
        super.onDestroy()
    }


}