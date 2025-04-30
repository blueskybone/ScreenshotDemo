package com.blueskybone.screenshotdemo.service

import android.app.Dialog
import android.content.Intent
import android.provider.Settings
import android.service.quicksettings.TileService
import com.blueskybone.screenshotdemo.R
import com.hjq.toast.Toaster

/**
 *   Created by blueskybone
 *   Date: 2024/4/1
 */
class QuickTileService : TileService() {
    override fun onClick() {
        super.onClick()
        collapsePanel()
        startScreenCapture()
    }

    private fun collapsePanel() {
        val dialog = Dialog(this)
        showDialog(dialog)
        dialog.dismiss()
    }

    private fun startScreenCapture(){
        if (!Settings.canDrawOverlays(this)) {
            Toaster.show(getString(R.string.float_window_permission_not_get))
            return
        }
        if (CapturePermission.intent == null) {
            val acquireIntent = Intent(this, AcquireCapturePermission::class.java)
            acquireIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(acquireIntent)
        } else {
            val intent = Intent(this, ScreenshotService::class.java)
            startService(intent)
        }
    }
}