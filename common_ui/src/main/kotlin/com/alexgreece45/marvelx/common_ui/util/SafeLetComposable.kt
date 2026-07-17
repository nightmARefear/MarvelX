package com.alexgreece45.marvelx.common_ui.util

import androidx.compose.runtime.Composable

/**
 * Executes [block] if [p1] and [p2] are not null and provides their non-null values,
 * otherwise `null` is returned.
 */
@Composable
fun <T1 : Any, T2 : Any, R : Any> safeLetComposable(
    p1: T1?,
    p2: T2?,
    block: @Composable (T1, T2) -> R?
): R? {
    return if (p1 != null && p2 != null) block(p1, p2) else null
}

/**
 * Executes [block] if [p1], [p2], and [p3] are not null and provides their non-null values,
 * otherwise `null` is returned.
 */
@Composable
fun <T1 : Any, T2 : Any, R : Any, T3 : Any> safeLetComposable(
    p1: T1?,
    p2: T2?,
    p3: T3?,
    block: @Composable (T1, T2, T3) -> R?
): R? {
    return if (p1 != null && p2 != null && p3 != null) block(p1, p2, p3) else null
}