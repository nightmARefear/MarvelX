package com.alexgreece45.marvelx.domain.model_generic

import android.net.Uri

/**
 * Contains all the file content data for an attached file.
 *
 * @param uri The URI of this file.
 * @param name The name of this file.
 * @param bytesCount The number of bytes of this file.
 */
data class AttachedFileContentData(
    val uri: Uri,
    val name: String,
    val bytesCount: Long
)
