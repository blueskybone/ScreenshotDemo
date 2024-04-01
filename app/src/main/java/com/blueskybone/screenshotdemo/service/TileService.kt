package com.blueskybone.screenshotdemo.service

import android.service.quicksettings.TileService
import com.blueskybone.screenshotdemo.util.CollapseDialog

/**
 *   Created by blueskybone
 *   Date: 2024/4/1
 */
class QuickTileService : TileService() {
    override fun onClick() {
        super.onClick()
        collapseAndStartScreenTask()
    }

    private fun collapseAndStartScreenTask() {
        val dialog = CollapseDialog(this)
        showDialog(dialog)
        dialog.startScreenTask(this)
        dialog.dismiss()
    }
}