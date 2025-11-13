package com.example.camerastyle

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Application类
 */
@HiltAndroidApp
class CameraStyleApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // 初始化Timber日志
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        Timber.d("CameraStyleApp initialized")
    }
}
