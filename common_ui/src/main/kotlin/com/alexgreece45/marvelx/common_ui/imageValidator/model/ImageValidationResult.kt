package com.alexgreece45.marvelx.common_ui.imageValidator.model

import com.alexgreece45.marvelx.model.model.generic.AttachedFileContentData

sealed interface ImageValidationResult {
    data class Success(val allImages: List<AttachedFileContentData>) : ImageValidationResult
    data object OverMaxSize : ImageValidationResult
    data object OverMaxCount : ImageValidationResult
    data object InvalidExtensionType : ImageValidationResult
    data object AttachmentError : ImageValidationResult
    data object AlreadyExists : ImageValidationResult
}