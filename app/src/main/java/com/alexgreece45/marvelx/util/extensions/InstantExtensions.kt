package com.alexgreece45.marvelx.util.extensions

import java.time.Instant

/**
 * Returns the difference in seconds between [this] and the given [instant].
 */
fun Instant.differenceInSeconds(instant: Instant) = this.epochSecond - instant.epochSecond

/**
 * Returns the difference in minutes between [this] and the given [instant].
 */
fun Instant.differenceInMinutes(instant: Instant) =
    (this.epochSecond / 60) - (instant.epochSecond / 60)