package com.alexgreece45.marvelx.data.repository.helper

/**
 * A generic wrapper for repository responses.
 */
sealed interface RepositoryPartialState<out T> {
    /**
     * Indicates that the data was successfully fetched.
     *
     * @param data  The data fetched. Note: this is a nullable value!
     */
    data class Success<T>(val data: T?) : RepositoryPartialState<T>

    /**
     * Indicates that an error occurred whilst retrieving the data.
     *
     * @param errorMessage  The error message.
     */
    data class Failed<T>(val errorMessage: String) : RepositoryPartialState<T>
}