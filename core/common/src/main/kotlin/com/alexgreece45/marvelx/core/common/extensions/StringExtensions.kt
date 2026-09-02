package com.alexgreece45.marvelx.core.common.extensions

/**
 * Checks whether this instance ends with any of the specified suffixes.
 *
 * @param suffixes The suffixes to check against.
 * @param ignoreCase Whether the case should be ignored. By default, it's `true`.
 */
fun String.endsWithAnySuffix(suffixes: List<String>, ignoreCase: Boolean = true): Boolean {
    return suffixes.any { suffix ->
        this.endsWith(
            suffix = suffix,
            ignoreCase = ignoreCase
        )
    }
}

fun String.withEuroSymbol(): String = "$this€"