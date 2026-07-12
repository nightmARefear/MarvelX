package com.alexgreece45.marvelx.common_ui.imageValidator.model

import com.alexgreece45.marvelx.model.model.generic.AttachedFileContentData

sealed interface MultiImageValidationResult {
    data class Success(val allImages: List<AttachedFileContentData>) : MultiImageValidationResult
    data class FileError(val error: ImageValidationError): MultiImageValidationResult
    data object OverMaxCount : MultiImageValidationResult
    data object AlreadyExists : MultiImageValidationResult
}