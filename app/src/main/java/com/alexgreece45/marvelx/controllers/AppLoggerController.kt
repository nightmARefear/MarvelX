package com.alexgreece45.marvelx.controllers

import com.alexgreece45.marvelx.configuration.AppBuildType
import com.alexgreece45.marvelx.configuration.ConfigLogic
import logcat.AndroidLogcatLogger
import logcat.LogPriority
import logcat.LogcatLogger
import logcat.asLog
import logcat.logcat
import javax.inject.Inject

interface AppLoggerController {
    /**
     * Installs [AndroidLogcatLogger] if it hasn't already been installed, and if the [AppBuildType]
     * isn't [AppBuildType.RELEASE].
     */
    fun installLogger()

    fun d(message: () -> String)
    fun d(tag: String, message: () -> String)

    fun i(message: () -> String)
    fun i(tag: String, message: () -> String)

    fun e(message: () -> String)
    fun e(tag: String, message: () -> String)
    fun e(tag: String, throwable: Throwable)
}

class AppLoggerControllerImpl @Inject constructor(
    private val configLogic: ConfigLogic
) : AppLoggerController {
    override fun installLogger() {
        if (LogcatLogger.isInstalled.not() && configLogic.appBuildType != AppBuildType.RELEASE) {
            LogcatLogger.install(AndroidLogcatLogger(LogPriority.VERBOSE))
        }
    }

    override fun d(message: () -> String) {
        d(tag = configLogic.appBuildType.name, message = message)
    }

    override fun d(tag: String, message: () -> String) {
        logcat(priority = LogPriority.DEBUG, tag = tag, message = message)
    }

    override fun i(message: () -> String) {
        i(tag = configLogic.appBuildType.name, message = message)
    }

    override fun i(tag: String, message: () -> String) {
        logcat(priority = LogPriority.INFO, message = message)
    }

    override fun e(message: () -> String) {
        e(tag = configLogic.appBuildType.name, message = message)
    }

    override fun e(tag: String, message: () -> String) {
        logcat(priority = LogPriority.ERROR, tag = tag, message = message)
    }

    override fun e(tag: String, throwable: Throwable) {
        e(tag = tag, message = { throwable.asLog() })
    }
}