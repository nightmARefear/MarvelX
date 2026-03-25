package com.alexgreece45.marvelx.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import com.alexgreece45.marvelx.domain.model_generic.AttachedFileContentData
import com.alexgreece45.marvelx.domain.model_generic.ReadFileContentData
import com.alexgreece45.marvelx.util.extensions.compressImage
import com.alexgreece45.marvelx.util.extensions.toCompressFormat

/**
 * Contains utility functions for file-related operations.
 */
object FileUtil {

    /**
     * Retrieves the basic data of a file. More specifically, the uri, name, and the length in bytes.
     */
    fun getFileContentData(uri: Uri?, context: Context): AttachedFileContentData? {
        if (uri == null || uri.toString().isBlank()) {
            return null
        }

        val fileName = getFileName(context, uri).orEmpty()
        if (fileName.isBlank()) {
            return null
        }

        val fileContents = decodeBitmapFromFileUri(context, uri)
        if (fileContents == null) {
            return null
        }

        val fileSize = getFileSize(context, uri)
        if (fileSize == null) {
            return null
        }

        return AttachedFileContentData(
            uri = uri,
            name = fileName,
            bytesCount = fileSize
        )
    }

    /**
     * Retrieves advanced data of a file. More specifically, its raw bytes.
     */
    fun getAdvancedFileContentData(
        file: AttachedFileContentData,
        context: Context
    ): ReadFileContentData? {
        val compressFormat = file.name.toCompressFormat()
        if (compressFormat == null) {
            return null
        }

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
     * Retrieves the file's name. If it could not be retrieved `null` is returned instead.
     */
    private fun getFileName(context: Context, uri: Uri): String? {
        if (uri.scheme == "content") {
            context.contentResolver.query(uri, null, null, null)?.use { cursor ->
                val columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (columnIndex != -1) {
                    cursor.moveToFirst()
                    return cursor.getString(columnIndex)
                }
            }
        }
        return null
    }

    /**
     * Attempts to locate the file with the specified file URI and if found decodes it into a bitmap.
     */
    private fun decodeBitmapFromFileUri(context: Context, uri: Uri): Bitmap? {
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
     * Attempts to extract the size of the file from the specified URI.
     */
    private fun getFileSize(context: Context, uri: Uri): Long? {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val columnIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (columnIndex != -1) {
                cursor.moveToFirst()
                return cursor.getLong(columnIndex)
            }
        }
        return null
    }

    /**
     * Converts the specified number of bytes to Megabytes.
     */
    fun getMegabytesFromBytes(bytes: Long): Float = bytes.toFloat() / (1024f * 1024f)
}
