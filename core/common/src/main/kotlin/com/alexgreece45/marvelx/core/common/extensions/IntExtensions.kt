package com.alexgreece45.marvelx.core.common.extensions

/**
 * Adds `0` as the start padding character so that the resulting length of the given [Int] is equal
 * to `2`.
 */
fun Int.pad(): String {
    return this.toString().padStart(2, '0')
}

fun Int.fromMbToBytes(): Long = this * 1024L * 1024L