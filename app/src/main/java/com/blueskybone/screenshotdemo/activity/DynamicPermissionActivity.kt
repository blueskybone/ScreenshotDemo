package com.blueskybone.screenshotdemo.activity

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import com.blueskybone.screenshotdemo.APP

/**
 *   Created by blueskybone
 *   Date: 2024/4/1
 */
class DynamicPermissionActivity : Activity() {
    companion object {
        const val SCREENSHOT_REQUEST_CODE = 10453
    }

    override fun onStart() {
        super.onStart()
        (getSystemService(Context.MEDIA_PROJECTION_SERVICE) as? MediaProjectionManager)?.apply {
            APP.setMediaProjectionManager(this)
            try {
                startActivityForResult(createScreenCaptureIntent(), SCREENSHOT_REQUEST_CODE)
            } catch (e: ActivityNotFoundException) {
                finish()
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (SCREENSHOT_REQUEST_CODE == requestCode) {
            if (RESULT_OK == resultCode) {
                data?.run {
                    (data.clone() as? Intent)?.apply {
                        APP.setScreenshotPermission(this)
                        APP.startScreenshot()
                    }
                }
            } else {
                APP.setScreenshotPermission(null)
            }
        }
        finish()
    }
}