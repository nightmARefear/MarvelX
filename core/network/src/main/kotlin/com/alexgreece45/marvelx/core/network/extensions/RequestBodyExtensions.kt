package com.alexgreece45.marvelx.core.network.extensions

import okhttp3.RequestBody
import okio.Buffer

/**
 * Converts this [RequestBody]'s body into a [String] in `JSON` format and returns it. If the
 * conversion fails, an empty [String] is returned instead.
 */
internal fun RequestBody?.toJsonString(): String {
    if (this == null) return ""

    val buffer = Buffer()
    return try {
        writeTo(buffer)
        buffer.readUtf8()
    } catch (_: Exception) {
        ""
    }
}