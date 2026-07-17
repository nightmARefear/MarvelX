package com.alexgreece45.marvelx.core.common.logger

interface AppLogger {
    /**
     * Installs the logger.
     */
    fun installLogger()

    fun d(message: () -> String)
    fun d(tag: String?, message: () -> String)

    fun i(message: () -> String)
    fun i(tag: String?, message: () -> String)

    fun e(message: () -> String)
    fun e(tag: String?, message: () -> String)
    fun e(tag: String?, throwable: Throwable)
}