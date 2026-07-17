package com.alexgreece45.marvelx.core.network.model.generic

sealed class ApiResult <out T> {

    /**
     * The call was successful. The data is stored in [body].
     */
    data class Success<T>(val body: T) : ApiResult<T>()

    /**
     * An application error has occurred. The error message is defined by [errorBody].
     */
    data class Error(val errorBody: String) : ApiResult<Nothing>()

    /**
     * A server error has occurred. The error body is defined by [failureMessage].
     */
    data class Failed(val failureMessage: String) : ApiResult<Nothing>()

    /**
     * A network related error has occurred.
     */
    data object NetworkError: ApiResult<Nothing>()
}