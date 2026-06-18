package com.alexgreece45.marvelx.core.data.extensions

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull

/**
 * Returns the file name extension.
 */
internal fun String.getFileNameExtension(): String =
    this.substringAfterLast(delimiter = ".", missingDelimiterValue = "").lowercase()

/**
 * Returns the media type from the given file name. If the extension is not supported, `null` is
 * returned instead.
 */
internal fun String.getMediaTypeFromFileName(): MediaType? {
    return when (this.getFileNameExtension()) {
        "jpg", "jpeg" -> "image/jpeg".toMediaTypeOrNull()
        "png" -> "image/png".toMediaTypeOrNull()
        else -> null
    }
}