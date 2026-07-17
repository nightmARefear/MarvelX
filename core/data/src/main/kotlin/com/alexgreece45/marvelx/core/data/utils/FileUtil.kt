package com.alexgreece45.marvelx.core.data.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.net.Uri
import com.alexgreece45.marvelx.core.common.constants.ConstantsContentTypes
import com.alexgreece45.marvelx.core.common.extensions.endsWithAnySuffix
import com.alexgreece45.marvelx.model.model.generic.AttachedFileContentData
import com.alexgreece45.marvelx.model.model.generic.ReadFileContentData
import java.io.ByteArrayOutputStream

object FileUtil {

    /**
     * Attempts to locate the file with the specified file URI and if found decodes it into a bitmap.
     */
    fun decodeBitmapFromFileUri(context: Context, uri: Uri): Bitmap? {
        return try {
            context.contentResolver
                .openInputStream(uri)
                ?.use { inputStream ->
                    BitmapFactory.decodeStream(inputStream)
                }
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Retrieves advanced data of a file. More specifically, its raw bytes.
     */
    fun getAdvancedFileContentData(
        file: AttachedFileContentData,
        context: Context
    ): ReadFileContentData? {
        val compressFormat = file.name.toCompressFormat() ?: return null

        val fileContent = decodeBitmapFromFileUri(context, file.uri)
            ?.compressImage(
                compressFormat = compressFormat
            )
        if (fileContent == null) {
            return null
        }

        return ReadFileContentData(
            fileName = file.name,
            fileContent = fileContent
        )
    }

    /**
     * Converts this file name into a [Bitmap.CompressFormat] based on the extension type. Only JPG and
     * PNG images are supported.
     */
    private fun String.toCompressFormat(): Bitmap.CompressFormat? {
        return when {
            this.endsWithAnySuffix(
                suffixes = ConstantsContentTypes.JPEG_FORMATS
            ) -> CompressFormat.JPEG

            this.endsWithAnySuffix(ConstantsContentTypes.PNG_FORMATS) -> Bitmap.CompressFormat.PNG

            else -> null
        }
    }

    /**
     * Compresses this instance and returns it as a [ByteArray].
     *
     * @param compressFormat The compression format.
     * @param imageQuality The final image quality compared to the original. It has the default value of
     * `75`.
     */
    private fun Bitmap.compressImage(
        compressFormat: CompressFormat,
        imageQuality: Int = 75
    ): ByteArray {
        val outputStream = ByteArrayOutputStream()
        this.compress(compressFormat, imageQuality, outputStream)
        return outputStream.toByteArray()
    }
}