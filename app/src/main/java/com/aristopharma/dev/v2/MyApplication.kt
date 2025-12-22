package com.aristopharma.v2.dev

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication() : Application() {
    override fun onCreate() {
        super.onCreate()

    }

}