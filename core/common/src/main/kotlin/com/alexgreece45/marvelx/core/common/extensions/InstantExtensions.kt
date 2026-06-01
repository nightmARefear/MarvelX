package com.alexgreece45.marvelx.core.common.extensions

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun Instant.minusMinutes(minutes: Long): Instant = this.minusSeconds(60 * minutes)
fun Instant.minusHours(hours: Long): Instant = this.minusMinutes(60 * hours)
fun Instant.minusDays(days: Long): Instant = this.minusHours(24 * days)

fun Instant.plusMinutes(minutes: Long): Instant = this.plusSeconds(60 * minutes)
fun Instant.plusHours(hours: Long): Instant = this.plusMinutes(60 * hours)
fun Instant.plusDays(days: Long): Instant = this.plusHours(24 * days)

/**
 * Returns the difference in milliseconds between this instance and the given [instant].
 */
fun Instant.differenceInMilliseconds(instant: Instant): Long =
    this.toEpochMilli() - instant.toEpochMilli()

/**
 * Returns the difference in seconds between this instance and the given [instant].
 */
fun Instant.differenceInSeconds(instant: Instant): Long = this.epochSecond - instant.epochSecond

/**
 * Returns the difference in minutes between [this] and the given [instant].
 */
fun Instant.differenceInMinutes(instant: Instant): Long =
    (this.epochSecond / 60) - (instant.epochSecond / 60)

/**
 * Converts this to a zoned date time using the system's default zone.
 */
fun Instant.toZonedDateTime(): ZonedDateTime = this.atZone(ZoneId.systemDefault())

fun Instant.toCustomFormat(pattern: String): String = try {
    this
        .toZonedDateTime()
        .format(DateTimeFormatter.ofPattern(pattern))
} catch (_: Exception) {
    ""
}

/**
 * Returns the default formatted instant.
 */
fun Instant.defaultFormat(): String = this.toCustomFormat("MMMM d, yyyy")

/**
 * Converts this to a listing format.
 */
fun Instant.toListingFormat(): String = this.toCustomFormat("MMMM d, yyyy HH:mm:ss")

/**
 * Converts this to a short Order format.
 */
fun Instant.toOrderFormatShort(): String = this.toCustomFormat("MMM dd yyyy")

/**
 * Converts this to a full Order format.
 */
fun Instant.toOrderFormatFull(): String = this.toCustomFormat("MMMM dd yyyy, HH:mm:ss")

/**
 * Converts this to a short Order estimated delivery format.
 */
fun Instant.toOrderEstimatedDeliveryFormatShort(): String = this.toCustomFormat("MMM dd")

/**
 * Converts this to a full Order estimated delivery format.
 */
fun Instant.toOrderEstimatedDeliveryFormatFull(): String = this.toCustomFormat("MMM dd, yyyy")