package com.alexgreece45.marvelx.common_ui.imageValidator

import com.alexgreece45.marvelx.common_ui.imageValidator.model.ImageData
import org.koin.core.annotation.Factory

interface ImageValidator {

    fun setRules(
        allowedExtensionTypes: List<String>,
        maxSize: Int
    )
    fun validateImages(images: List<ImageData>)
}

@Factory
internal class ImageValidatorImpl : ImageValidator {

    private var allowedExtensions: List<String> = emptyList()
    private var maxImageSize: Int = 0

    override fun setRules(
        allowedExtensionTypes: List<String>,
        maxSize: Int
    ) {
        allowedExtensions = allowedExtensionTypes
        maxImageSize = maxSize
    }

    override fun validateImages(images: List<ImageData>) {
        return images.forEach { image ->


        }
    }
}