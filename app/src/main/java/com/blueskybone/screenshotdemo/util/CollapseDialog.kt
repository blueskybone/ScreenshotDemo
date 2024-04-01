package com.blueskybone.screenshotdemo.util

import android.app.Dialog
import android.content.Context
import com.blueskybone.screenshotdemo.APP

/**
 *   Created by blueskybone
 *   Date: 2024/4/1
 */

class CollapseDialog(context: Context):Dialog(context) {
    fun startScreenTask(context: Context) {
        APP.startScreenTask(context)
    }
}