package com.alexgreece45.marvelx.core.data.helper.multi_part_builder.model

import com.alexgreece45.marvelx.model.model.generic.AttachedFileContentData

sealed interface MultipartItem {
    data class Text(val partName: String, val value: String) : MultipartItem

    data class Image(
        val partName: String,
        val fileName: String,
        val imageFileData: AttachedFileContentData
    ) : MultipartItem
}