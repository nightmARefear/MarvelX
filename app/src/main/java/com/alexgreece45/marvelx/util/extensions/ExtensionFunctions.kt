package com.alexgreece45.marvelx.util.extensions

import android.util.Patterns
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.MaterialTheme
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.Dp
import com.alexgreece45.marvelx.util.DEFAULT_SCREEN_PADDING
import com.google.accompanist.placeholder.PlaceholderDefaults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import java.math.BigInteger
import java.security.MessageDigest
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId

fun <T> Flow<T>.safeAsync(with: (Throwable) -> (T)): Flow<T> {
    return this.flowOn(Dispatchers.IO).catch { emit(with(it)) }
}

fun String.toMd5(): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(this.toByteArray())).toString(16).padStart(32, '0')
}

fun String.isEmail(): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun OffsetDateTime.toSystemLocalDateTime(): LocalDateTime =
    this.atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()

/**
 * Used to return an alternative non-null value, if the given value
 * was null. This extension function is a replacement for the
 * elvis operator.
 *
 * @param other the alternative value
 *
 * @return returns the first value if it is not null, otherwise
 * the value "other"
 * */
inline fun <reified Any> Any?.or(other: Any): Any = this ?: other

//fun LocalDateTime.toOffsetDateTimeUTC() : OffsetDateTime = OffsetDateTime.parse(this.toString())
fun LocalDateTime.toOffsetDateTime(): OffsetDateTime = OffsetDateTime.parse(this.toString())

@Composable
fun PaddingValues.applyPadding(padding: Dp = DEFAULT_SCREEN_PADDING): PaddingValues {
    return PaddingValues(
        start = padding,
        top = padding + this.calculateTopPadding(),
        end = padding,
        bottom = padding + this.calculateBottomPadding()
    )
}

@Composable
fun PlaceholderDefaults.color(
    backgroundColor: Color = MaterialTheme.colors.surface,
    contentColor: Color = contentColorFor(backgroundColor),
    contentAlpha: Float = 0.1f,
): Color = contentColor.copy(contentAlpha).compositeOver(backgroundColor)

@Composable
fun PlaceholderDefaults.shimmerHighlightColor(
    backgroundColor: Color = MaterialTheme.colors.surface,
    alpha: Float = 0.75f,
): Color {
    return backgroundColor.copy(alpha = alpha)
}