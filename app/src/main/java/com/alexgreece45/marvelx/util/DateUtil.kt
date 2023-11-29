package com.alexgreece45.marvelx.util

import com.alexgreece45.marvelx.util.extensions.pad
import java.time.Instant

object DateUtil {

    /**
     * Gets the number of seconds from the Java epoch of 1970-01-01T00:00:00Z.
     *
     * @return the seconds from the epoch of 1970-01-01T00:00:00Z
     * */
    fun getSecondsFromEpoch(): Long = Instant.now().epochSecond

    /**
     * Converts the given [minutes] and [seconds] into a human readable format.
     *
     * ### Examples:
     *
     * (1, 10)  -> 1:10
     *
     * (1, 2)   -> 1:02
     *
     * (1, 1)   -> 1:01
     *
     * (1, 0)   -> 1:00
     *
     * (0, 59)  -> 59
     *
     * (0, 10)  -> 10
     *
     * (0, 9)   -> 9
     *
     * (0, 1)   -> 1
     *
     * (0, 0)   -> 0
     */
    fun formatTime(minutes: Int, seconds: Int): String {
        val shouldReturnMinutes = minutes != 0
        val shouldAddStartPaddingAtSeconds = shouldReturnMinutes

        var finalTime = ""
        if (shouldReturnMinutes) finalTime += "$minutes:"
        finalTime += when (shouldAddStartPaddingAtSeconds) {
            true -> seconds.pad()
            false -> seconds
        }
        return finalTime
    }
}