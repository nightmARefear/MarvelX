package com.alexgreece45.marvelx.core.network.utils

import okhttp3.MultipartBody
import okio.Buffer

internal object MultipartBodyUtils {
    fun getMultipartValueByName(multipartBody: MultipartBody, targetPartName: String): String? {
        for (part in multipartBody.parts) {
            val contentDisposition = part.headers?.get("Content-Disposition") ?: continue
            val currentPartName = substringBetween(contentDisposition, "name=\"", "\"")

            if (currentPartName == targetPartName) {
                val buffer = Buffer()
                part.body.writeTo(buffer)
                return buffer.readUtf8()
            }
        }

        return null
    }

    // Helper function to extract the string between quotes
    private fun substringBetween(str: String, prefix: String, suffix: String): String? {
        val start = str.indexOf(prefix)
        if (start == -1) return null
        val end = str.indexOf(suffix, start + prefix.length)
        if (end == -1) return null
        return str.substring(start + prefix.length, end)
    }
}