package com.alexgreece45.marvelx.common_ui.imageValidator.model

data class ImageValidationConfig(
    val allowedExtensions: List<String> = emptyList(),
    val maxCount: Int = 0,
    val maxSizeMB: Int = 0
)