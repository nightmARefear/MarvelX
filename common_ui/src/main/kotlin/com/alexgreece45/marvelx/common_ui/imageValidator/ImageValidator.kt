package com.alexgreece45.marvelx.common_ui.imageValidator

import com.alexgreece45.marvelx.common_ui.imageValidator.model.ImageValidationConfig
import com.alexgreece45.marvelx.common_ui.imageValidator.model.ImageValidationResult
import com.alexgreece45.marvelx.core.common.extensions.endsWithAnySuffix
import com.alexgreece45.marvelx.core.common.extensions.fromMbToBytes
import com.alexgreece45.marvelx.model.model.generic.AttachedFileContentData
import org.koin.core.annotation.Factory

interface ImageValidator {
    fun validateImages(
        oldImages: List<AttachedFileContentData>,
        newImages: List<AttachedFileContentData>?,
        config: ImageValidationConfig
    ): ImageValidationResult
}

@Factory
internal class ImageValidatorImpl : ImageValidator {

    override fun validateImages(
        oldImages: List<AttachedFileContentData>,
        newImages: List<AttachedFileContentData>?,
        config: ImageValidationConfig
    ): ImageValidationResult {
        if (newImages.isNullOrEmpty()) {
            return ImageValidationResult.AttachmentError
        }
        if (oldImages.size + newImages.size > config.maxCount) {
            return ImageValidationResult.OverMaxCount
        }

        val oldImageUris = oldImages.map { it.uri }
        newImages.forEach { image ->
            if (image.uri in oldImageUris) {
                return ImageValidationResult.AlreadyExists
            }

            if (!image.name.endsWithAnySuffix(config.allowedExtensions)) {
                return ImageValidationResult.InvalidExtensionType
            }

            if (image.bytesCount > config.maxSizeMB.fromMbToBytes()) {
                return ImageValidationResult.OverMaxSize
            }
        }

        return ImageValidationResult.Success(allImages = oldImages + newImages)
    }
}