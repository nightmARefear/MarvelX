package com.alexgreece45.marvelx.util.extensions

import okhttp3.RequestBody
import java.io.IOException

/**
 * Converts this [RequestBody]'s body into a [String] in `JSON` format. If the conversion fails, an
 * empty [String] is returned.
 */
fun RequestBody?.toJsonString(): String {
    if (this == null) return ""

    val buffer = okio.Buffer()
    return try {
        writeTo(buffer)
        buffer.readUtf8()
    } catch (e: IOException) {
        ""
    }
}