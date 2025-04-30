package com.blueskybone.screenshotdemo.service

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager

class AcquireCapturePermission : Activity() {


    companion object {
        const val SCREENSHOT_REQUEST_CODE = 10453
    }

    override fun onStart() {
        super.onStart()
        (getSystemService(Context.MEDIA_PROJECTION_SERVICE) as? MediaProjectionManager)?.apply {
            startActivityForResult(createScreenCaptureIntent(), SCREENSHOT_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (SCREENSHOT_REQUEST_CODE == requestCode) {
            if (RESULT_OK == resultCode) {
                CapturePermission.intent = data
                val intent = Intent(this, ScreenshotService::class.java)
                intent.putExtra("setPermission", true)
                startForegroundService(intent)
                finish()
            }
            finish()
        }
    }
}