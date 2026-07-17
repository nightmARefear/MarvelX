package com.alexgreece45.marvelx.util.extensions

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Converts a user related timestamp to a human-readable format.
 */
fun Long.toUserFormat(): String = Instant.ofEpochMilli(this)
    .atZone(ZoneId.systemDefault())
    .format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))
