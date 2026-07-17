package com.alexgreece45.marvelx.core.security.controllers

import org.koin.core.annotation.Single
import java.io.File

internal interface AntiHookController {

    /**
     * Checks if an external process is attached to the application's process and returns `true` if
     * it is, otherwise `false`.
     */
    fun isExternalProcessAttached(): Boolean
}

@Single
internal class AntiHookControllerImpl : AntiHookController {

    override fun isExternalProcessAttached(): Boolean {
        return try {
            val status = File("/proc/self/status").readText()
            val tracerPid = status.lines()
                .find { it.startsWith("TracerPid:") }
                ?.split(":")
                ?.get(1)
                ?.trim()
                ?.toInt()
            val isExternalProcessAttached = tracerPid != null && tracerPid > 0
            isExternalProcessAttached
        } catch (_: Exception) {
            false
        }
    }
}