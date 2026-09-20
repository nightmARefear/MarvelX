package com.alexgreece45.marvelx.core.common.util

import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit

/**
 * A coarse "how long ago" bucket. Kept free of strings so the UI layer can localise it.
 */
sealed interface RelativeTime {
    data object JustNow : RelativeTime
    data class Minutes(val minutes: Long) : RelativeTime
    data class Hours(val hours: Long) : RelativeTime
    data object Yesterday : RelativeTime
    data class Date(val instant: Instant, val isCurrentYear: Boolean) : RelativeTime
}

fun Instant.toRelativeTime(
    now: Instant = Instant.now(),
    zoneId: ZoneId = ZoneId.systemDefault()
): RelativeTime {
    val elapsedMinutes = ChronoUnit.MINUTES.between(this, now)
    val thisDate = this.atZone(zoneId).toLocalDate()
    val today = now.atZone(zoneId).toLocalDate()

    return when {
        elapsedMinutes < 1 -> RelativeTime.JustNow
        elapsedMinutes < 60 -> RelativeTime.Minutes(elapsedMinutes)
        thisDate == today -> RelativeTime.Hours(ChronoUnit.HOURS.between(this, now))
        thisDate == today.minusDays(1) -> RelativeTime.Yesterday
        else -> RelativeTime.Date(instant = this, isCurrentYear = thisDate.year == today.year)
    }
}
