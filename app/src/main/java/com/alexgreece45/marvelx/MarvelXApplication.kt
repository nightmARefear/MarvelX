package com.alexgreece45.marvelx

import dagger.hilt.android.HiltAndroidApp
import logcat.AndroidLogcatLogger
import logcat.LogPriority
import logcat.LogcatLogger

@HiltAndroidApp
class MarvelXApplication : android.app.Application() {
    override fun onCreate() {
        super.onCreate()

        if (LogcatLogger.isInstalled.not() && BuildConfig.LOG_MODE) {
            LogcatLogger.install(AndroidLogcatLogger(LogPriority.DEBUG))
        }
    }
}