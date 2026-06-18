package com.alexgreece45.marvelx.common_ui.imageValidator.model

sealed interface ImageValidationResult {
    data object OverMaxSize : ImageValidationResult
    data object InvalidExtensionType : ImageValidationResult
    data object Success : ImageValidationResult
}