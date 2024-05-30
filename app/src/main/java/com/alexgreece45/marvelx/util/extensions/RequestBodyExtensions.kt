package com.alexgreece45.marvelx.util.extensions

import okhttp3.RequestBody

/**
 * Converts this [RequestBody]'s body into a [String] in `JSON` format and returns it. If the
 * conversion fails, an empty [String] is returned instead.
 */
fun RequestBody?.toJsonString(): String {
    if (this == null) return ""

    val buffer = okio.Buffer()
    return try {
        writeTo(buffer)
        buffer.readUtf8()
    } catch (e: Exception) {
        ""
    }
}