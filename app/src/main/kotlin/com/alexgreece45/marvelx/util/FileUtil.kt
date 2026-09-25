package com.alexgreece45.marvelx.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.alexgreece45.marvelx.core.data.utils.FileUtil
import com.alexgreece45.marvelx.model.model.generic.AttachedFileContentData

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

        // Only the image header is read here; the full image is decoded (downsampled) on upload.
        if (!FileUtil.isImageFile(context, uri)) {
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
