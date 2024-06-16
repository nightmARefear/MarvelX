package com.alexgreece45.marvelx.util.extensions

/**
 * Converts the given number of bytes to megabytes (MB).
 */
fun Long.fromBytesToMb(): Long = this / (1024L * 1024L)

/**
 * Converts the given number of megabytes (MB) to bytes.
 */
fun Long.fromMbToBytes(): Long = this * 1024L * 1024L