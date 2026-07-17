package com.alexgreece45.marvelx.common_ui.imageValidator

import com.alexgreece45.marvelx.common_ui.imageValidator.model.ImageValidationConfig
import com.alexgreece45.marvelx.common_ui.imageValidator.model.ImageValidationError
import com.alexgreece45.marvelx.common_ui.imageValidator.model.MultiImageValidationResult
import com.alexgreece45.marvelx.common_ui.imageValidator.model.SingleImageValidationResult
import com.alexgreece45.marvelx.core.common.extensions.endsWithAnySuffix
import com.alexgreece45.marvelx.core.common.extensions.fromMbToBytes
import com.alexgreece45.marvelx.model.model.generic.AttachedFileContentData
import org.koin.core.annotation.Factory

interface ImageValidator {
    fun validateImage(
        image: AttachedFileContentData?,
        config: ImageValidationConfig
    ): SingleImageValidationResult

    fun validateImages(
        oldImages: List<AttachedFileContentData>,
        newImages: List<AttachedFileContentData>?,
        config: ImageValidationConfig
    ): MultiImageValidationResult
}

@Factory
internal class ImageValidatorImpl : ImageValidator {

    override fun validateImage(
        image: AttachedFileContentData?,
        config: ImageValidationConfig
    ): SingleImageValidationResult {
        if (image == null) {
            return SingleImageValidationResult.Error(ImageValidationError.AttachmentError)
        }

        if (!image.name.endsWithAnySuffix(config.allowedExtensions)) {
            return SingleImageValidationResult.Error(ImageValidationError.InvalidExtensionType)
        }

        if (image.bytesCount > config.maxSizeMB.fromMbToBytes()) {
            return SingleImageValidationResult.Error(ImageValidationError.OverMaxSize)
        }

        return SingleImageValidationResult.Success(image)
    }

    override fun validateImages(
        oldImages: List<AttachedFileContentData>,
        newImages: List<AttachedFileContentData>?,
        config: ImageValidationConfig
    ): MultiImageValidationResult {
        if (newImages.isNullOrEmpty()) {
            return MultiImageValidationResult.FileError(ImageValidationError.AttachmentError)
        }
        if (oldImages.size + newImages.size > config.maxCount) {
            return MultiImageValidationResult.OverMaxCount
        }

        val oldImageUris = oldImages.map { it.uri }
        newImages.forEach { image ->
            if (image.uri in oldImageUris) {
                return MultiImageValidationResult.AlreadyExists
            }

            val validateImageResult = validateImage(
                image = image,
                config = config
            )
            when (validateImageResult) {
                is SingleImageValidationResult.Error -> {
                    when (validateImageResult.error) {
                        ImageValidationError.OverMaxSize -> return MultiImageValidationResult.FileError(
                            ImageValidationError.OverMaxSize
                        )

                        ImageValidationError.InvalidExtensionType -> return MultiImageValidationResult.FileError(
                            ImageValidationError.InvalidExtensionType
                        )

                        ImageValidationError.AttachmentError -> {
                            // Already checked before
                        }
                    }
                }

                is SingleImageValidationResult.Success -> {}
            }
        }

        return MultiImageValidationResult.Success(allImages = oldImages + newImages)
    }
}