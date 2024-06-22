package com.alexgreece45.marvelx.util.extensions

import java.time.Instant

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