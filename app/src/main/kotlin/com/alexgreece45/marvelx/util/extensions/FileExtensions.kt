package com.alexgreece45.marvelx.util.extensions

import android.webkit.MimeTypeMap
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

/**
 * Converts this [File] to a [RequestBody] if its extension is supported.
 *
 * @param supportedFileExtensions A list of file extensions that are considered valid for conversion.
 *
 * @return A [RequestBody] representing the file content, with the media type determined from the
 * file extension.
 *
 * @throws IllegalArgumentException If the file extension is not in the list of supported extensions.
 */
fun File.toRequestBody(supportedFileExtensions: List<String>): RequestBody {
    val fileExtension = this.extension.lowercase()
    if (fileExtension !in supportedFileExtensions) throw IllegalArgumentException("Unsupported file extension $fileExtension ")
    return this.asRequestBody(this.getMediaType())
}

/**
 * Determines the media type of this [File] based on its extension.
 *
 * Utilizes [MimeTypeMap] to map the file extension to a corresponding MIME type, which is then
 * parsed into a [MediaType] object.
 *
 * @return The [MediaType] of the file if recognized, or `null` if the file extension is not
 * associated with a known media type.
 */
fun File.getMediaType(): MediaType? =
    MimeTypeMap.getSingleton().getMimeTypeFromExtension(this.extension.lowercase())
        ?.toMediaTypeOrNull()