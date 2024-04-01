package com.blueskybone.screenshotdemo.util

import com.blueskybone.screenshotdemo.APP

/**
 *   Created by blueskybone
 *   Date: 2024/4/1
 */
fun stringRes(resId: Int, vararg formatArgs: Any): String {
    return APP.resources.getString(resId)
}
