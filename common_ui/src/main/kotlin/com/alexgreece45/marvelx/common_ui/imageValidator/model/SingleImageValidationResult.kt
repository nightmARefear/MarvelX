package com.alexgreece45.marvelx.common_ui.imageValidator.model

import com.alexgreece45.marvelx.model.model.generic.AttachedFileContentData

sealed interface SingleImageValidationResult {
    data class Success(val image: AttachedFileContentData) : SingleImageValidationResult
    data class Error(val error: ImageValidationError) : SingleImageValidationResult
}