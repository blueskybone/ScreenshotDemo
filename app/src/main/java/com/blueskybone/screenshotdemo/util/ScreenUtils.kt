package com.blueskybone.screenshotdemo.util

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager

/**
 *   Created by blueskybone
 *   Date: 2024/4/1
 */
fun getRealScreenSize(context: Context): Point {
    val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val bounds = wm.currentWindowMetrics.bounds
        Point(
            bounds.width(),
            bounds.height()
        )

    } else {
        Point().apply {
            @Suppress("DEPRECATED")
            wm.defaultDisplay.getSize(this)
        }
    }
}

fun getDensityDpi(context: Context): Int {
    val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val metrics = DisplayMetrics()
    @Suppress("DEPRECATED")
    wm.defaultDisplay.getRealMetrics(metrics)
    return metrics.densityDpi
}