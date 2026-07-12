package com.alexgreece45.marvelx.common_ui.imageValidator.model

sealed interface ImageValidationError {
    data object OverMaxSize : ImageValidationError
    data object InvalidExtensionType : ImageValidationError
    data object AttachmentError : ImageValidationError
}