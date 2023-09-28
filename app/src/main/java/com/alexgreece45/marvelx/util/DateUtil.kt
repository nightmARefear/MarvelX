package com.alexgreece45.marvelx.util

import java.time.Instant

object DateUtil {
    fun getTimestamp(): Long = Instant.EPOCH.epochSecond
}