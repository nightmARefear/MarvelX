package com.alexgreece45.marvelx.util.extensions

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import java.io.ByteArrayOutputStream

/**
 * Compresses this instance and returns it as a [ByteArray].
 *
 * @param compressFormat The compression format.
 * @param imageQuality The final image quality compared to the original. It has the default value of
 * `25`.
 */
internal fun Bitmap.compressImage(
    compressFormat: CompressFormat,
    imageQuality: Int = 75
): ByteArray {
    val outputStream = ByteArrayOutputStream()
    this.compress(compressFormat, imageQuality, outputStream)
    return outputStream.toByteArray()
}
