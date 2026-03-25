package com.alexgreece45.marvelx.util

import com.alexgreece45.marvelx.util.extensions.pad
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth

object DateUtil {

    /**
     * Returns the current UTC [Instant].
     */
    fun getUTCInstant(): Instant = Instant.now()

    /**
     * Returns the number of seconds from the Java epoch of 1970-01-01T00:00:00Z.
     *
     * @return the seconds from the epoch of 1970-01-01T00:00:00Z
     * */
    fun getEpochSeconds(): Long = getUTCInstant().epochSecond

    /**
     * Returns the epoch milliseconds.
     */
    fun getEpochMillis() = getUTCInstant().toEpochMilli()

    /**
     * Returns the current local date.
     */
    fun getCurrentLocalDate(): LocalDate = LocalDate.now()

    /**
     * Returns the current year and month.
     */
    fun getYearMonth(): YearMonth = YearMonth.now()

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
