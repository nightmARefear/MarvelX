package com.alexgreece45.marvelx.util

import java.time.Instant

object DateUtil {

    /**
     * Gets the number of seconds from the Java epoch of 1970-01-01T00:00:00Z.
     *
     * @return the seconds from the epoch of 1970-01-01T00:00:00Z
     * */
    fun getSecondsFromEpoch(): Long = Instant.now().epochSecond
}