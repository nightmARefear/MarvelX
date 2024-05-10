package com.alexgreece45.marvelx.util.extensions

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.util.DEFAULT_SCREEN_PADDING
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId

fun <T> Flow<T>.safeAsync(with: (Throwable) -> (T)): Flow<T> {
    return this.flowOn(Dispatchers.IO).catch { emit(with(it)) }
}

fun OffsetDateTime.toSystemLocalDateTime(): LocalDateTime =
    this.atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()

fun LocalDateTime.toOffsetDateTime(): OffsetDateTime = OffsetDateTime.parse(this.toString())

/**
 * Adds `0` as the start padding character so that the resulting length of the given [Int] is equal
 * to `2`.
 */
fun Int.pad(): String {
    return this.toString().padStart(2, '0')
}

/**
 * Applies padding to all the edges inside a box.
 */
@Composable
fun PaddingValues.applyPadding(padding: Dp = DEFAULT_SCREEN_PADDING): PaddingValues {
    return PaddingValues(
        start = padding,
        top = padding + this.calculateTopPadding(),
        end = padding,
        bottom = padding + this.calculateBottomPadding()
    )
}

/**
 * Applies padding to the `start` and `end` edges inside a box.
 */
@Composable
fun screenPaddingsSte(padding: Dp = DEFAULT_SCREEN_PADDING): PaddingValues {
    return PaddingValues(
        start = padding,
        top = 0.dp,
        end = padding,
        bottom = 0.dp
    )
}

/**
 * Applies `0.`[dp] padding to all the edges inside a box.
 */
@Composable
fun screenPaddingsNone(): PaddingValues =
    PaddingValues(all = 0.dp)