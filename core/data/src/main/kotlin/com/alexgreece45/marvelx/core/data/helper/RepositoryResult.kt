package com.alexgreece45.marvelx.core.data.helper

/**
 * A generic wrapper for repository results.
 */
sealed class RepositoryResult<out T> {
    /**
     * The requested data was successfully fetched. [data] denotes the fetched data.
     */
    data class Success<T>(val data: T) : RepositoryResult<T>()

    /**
     * An error occurred while fetching the data. The relevant error message is defined in
     * [failureMessage].
     */
    data class Failed(val failureMessage: String) : RepositoryResult<Nothing>()

    /**
     * A network related error has occurred while fetching the data.
     */
    data object NetworkError : RepositoryResult<Nothing>()
}

inline fun <T> RepositoryResult<T>.onSuccess(action: (T) -> Unit): RepositoryResult<T> {
    if (this is RepositoryResult.Success) action(data)
    return this
}

inline fun <T> RepositoryResult<T>.onFailed(action: (String) -> Unit): RepositoryResult<T> {
    if (this is RepositoryResult.Failed) action(failureMessage)
    return this
}

inline fun <T> RepositoryResult<T>.onNetworkError(action: () -> Unit): RepositoryResult<T> {
    if (this is RepositoryResult.NetworkError) action()
    return this
}