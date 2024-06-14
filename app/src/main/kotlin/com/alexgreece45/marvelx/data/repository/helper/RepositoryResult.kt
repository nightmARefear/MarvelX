package com.alexgreece45.marvelx.data.repository.helper

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