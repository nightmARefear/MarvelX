package com.alexgreece45.marvelx.core.data.helper.multi_part_builder.model

import com.alexgreece45.marvelx.core.data.helper.RepositoryResult

/**
 * The exposed multipart result paired with the related API call.
 */
sealed interface MultipartResult<out T> {
    data class Success<out T>(val data: T) : MultipartResult<T>
    data class GenericError(val errorMessage: String) : MultipartResult<Nothing>
    data object NetworkError : MultipartResult<Nothing>
    data object ImageProcessingError : MultipartResult<Nothing>
}

fun <T> RepositoryResult<T>.toMultipartResult(): MultipartResult<T> =
    when (this) {
        is RepositoryResult.Success -> MultipartResult.Success(this.data)
        is RepositoryResult.Failed -> MultipartResult.GenericError(this.failureMessage)
        RepositoryResult.NetworkError -> MultipartResult.NetworkError
    }

fun <T> MultipartBuilderResult<T>.toMultipartResult(): MultipartResult<T> =
    when (this) {
        is MultipartBuilderResult.Success -> MultipartResult.Success(this.data)
        is MultipartBuilderResult.GenericError -> MultipartResult.GenericError(this.errorMessage)
        MultipartBuilderResult.ImageProcessingError -> MultipartResult.ImageProcessingError
    }