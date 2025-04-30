package com.blueskybone.screenshotdemo

import android.app.Application
import android.view.Gravity
import com.hjq.toast.Toaster
import com.hjq.toast.style.BlackToastStyle

/**
 *   Created by blueskybone
 *   Date: 2024/4/1
 */
lateinit var APP: App

class App : Application() {

    init {
        Toaster.init(this)
        APP = this
    }

    override fun onCreate() {
        Toaster.setStyle(BlackToastStyle())
        Toaster.setGravity(Gravity.TOP, 0, 180)
        super.onCreate()
    }
}