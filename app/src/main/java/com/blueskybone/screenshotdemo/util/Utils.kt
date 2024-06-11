package com.blueskybone.screenshotdemo.util

import com.blueskybone.screenshotdemo.APP
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 *   Created by blueskybone
 *   Date: 2024/6/11
 */

fun getFormatDate(pattern: String = "yyyyMMdd_HH:mm:ss"): String {
    val current = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return current.format(formatter)
}

fun stringRes(resId: Int, vararg formatArgs: Any): String {
    return APP.resources.getString(resId)
}
