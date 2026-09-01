package com.alexgreece45.marvelx.core.common.extensions

private const val ONE_MB_BYTES = 1024L * 1024L

/**
 * Converts the given number of bytes to megabytes (MB).
 */
fun Long.fromBytesToMb(): Long = this / ONE_MB_BYTES

/**
 * Converts the given number of megabytes (MB) to bytes.
 */
fun Long.fromMbToBytes(): Long = this * ONE_MB_BYTES

/**
 * Converts these milliseconds to epoch days.
 */
fun Long.toEpochDays(): Long = this / 1000 / 3600 / 24