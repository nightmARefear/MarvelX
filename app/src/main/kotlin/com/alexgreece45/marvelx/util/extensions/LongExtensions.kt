package com.alexgreece45.marvelx.util.extensions

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Converts the given number of bytes to megabytes (MB).
 */
fun Long.fromBytesToMb(): Long = this / (1024L * 1024L)

/**
 * Converts the given number of megabytes (MB) to bytes.
 */
fun Long.fromMbToBytes(): Long = this * 1024L * 1024L

/**
 * Converts a user related timestamp to a human readable format.
 */
fun Long.toUserFormat(): String = Instant.ofEpochMilli(this)
    .atZone(ZoneId.systemDefault())
    .format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))
