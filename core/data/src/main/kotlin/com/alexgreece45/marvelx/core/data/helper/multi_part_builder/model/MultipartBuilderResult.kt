package com.alexgreece45.marvelx.core.data.helper.multi_part_builder.model

import okhttp3.MultipartBody

sealed interface MultipartBuilderResult<out T> {
    data class Success<out T>(val data: T) : MultipartBuilderResult<T>
    data class GenericError(val errorMessage: String) : MultipartBuilderResult<Nothing>
    data object ImageProcessingError : MultipartBuilderResult<Nothing>
}

typealias MultipartPartBuilderResult = MultipartBuilderResult<MultipartBody.Part>
typealias MultipartBodyBuilderResult = MultipartBuilderResult<MultipartBody>