package com.alexgreece45.marvelx.core.common.logger

import androidx.annotation.GuardedBy
import logcat.AndroidLogcatLogger
import logcat.LogPriority
import logcat.LogcatLogger
import logcat.asLog
import logcat.logcat
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Single

@Factory
internal class AppLoggerImpl : AppLogger {

    private val lock = Any()

    @Volatile
    @GuardedBy("lock")
    private var hasBeenInstalled = false

    override fun installLogger() {
        if (hasBeenInstalled) return

        synchronized(lock) {
            if (!LogcatLogger.isInstalled) {
                LogcatLogger.loggers += AndroidLogcatLogger(LogPriority.VERBOSE)
                LogcatLogger.install()
                hasBeenInstalled = true
            }
        }
    }

    override fun d(message: () -> String) {
        d(tag = null, message = message)
    }

    override fun d(tag: String?, message: () -> String) {
        logcat(priority = LogPriority.DEBUG, tag = tag, message = message)
    }

    override fun i(message: () -> String) {
        i(tag = null, message = message)
    }

    override fun i(tag: String?, message: () -> String) {
        logcat(priority = LogPriority.INFO, tag = tag, message = message)
    }

    override fun e(message: () -> String) {
        e(tag = null, message = message)
    }

    override fun e(tag: String?, message: () -> String) {
        logcat(priority = LogPriority.ERROR, tag = tag, message = message)
    }

    override fun e(tag: String?, throwable: Throwable) {
        e(tag = tag, message = { throwable.asLog() })
    }
}