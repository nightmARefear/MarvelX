package com.alexgreece45.marvelx.ui.screens.chat.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.core.common.extensions.toCustomFormat
import com.alexgreece45.marvelx.core.common.util.RelativeTime
import java.time.LocalDate

@Composable
fun RelativeTime.label(): String =
    when (this) {
        RelativeTime.JustNow -> stringResource(R.string.relative_time_just_now)
        is RelativeTime.Minutes -> stringResource(R.string.relative_time_minutes, minutes)
        is RelativeTime.Hours -> stringResource(R.string.relative_time_hours, hours)
        RelativeTime.Yesterday -> stringResource(R.string.relative_time_yesterday)
        is RelativeTime.Date -> instant.toCustomFormat(if (isCurrentYear) "d MMM" else "d MMM yyyy")
    }

@Composable
fun LocalDate.dayLabel(today: LocalDate = LocalDate.now()): String =
    when (this) {
        today -> stringResource(R.string.relative_time_today)
        today.minusDays(1) -> stringResource(R.string.relative_time_yesterday)
        else -> this.toString().let { _ ->
            java.time.format.DateTimeFormatter.ofPattern(
                if (year == today.year) "EEEE, d MMMM" else "d MMMM yyyy"
            ).format(this)
        }
    }
