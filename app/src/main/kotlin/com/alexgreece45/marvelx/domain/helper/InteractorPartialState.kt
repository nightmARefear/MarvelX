package com.alexgreece45.marvelx.domain.helper

/**
 * A generic wrapper for interactor responses.
 */
sealed interface InteractorPartialState<out T> {
    /**
     * Indicates that the data was successfully retrieved.
     *
     * @param data  The data.
     */
    data class Success<T>(val data: T) : InteractorPartialState<T>

    /**
     * Indicates that an error occurred whilst retrieving the data. This is typically used when
     * the authentication fails, or when the app has sent invalid data; probably indicating a
     * mismatch in API models, or something else.
     *
     * @param failureMessage    The failure message.
     */
    data class Failed<T>(val failureMessage: String) : InteractorPartialState<T>

    /**
     * Indicates that an in-app error occurred whilst retrieving the data. This is typically used
     * when the `.toDomain()` fails, or for parsing - related exceptions.
     *
     * @param errorMessage  The error message.
     */
    data class Error<T>(val errorMessage: String) : InteractorPartialState<T>

    /**
     * Indicates that a network-related issue occurred whilst retrieving the data.
     */
    data object NoInternetConnection : InteractorPartialState<Nothing>
}

inline fun <T> InteractorPartialState<T>.onSuccess(action: (T) -> Unit): InteractorPartialState<T> {
    if (this is InteractorPartialState.Success) action(data)
    return this
}

inline fun <T> InteractorPartialState<T>.onFailed(action: (failureMessage: String) -> Unit): InteractorPartialState<T> {
    if (this is InteractorPartialState.Failed) action(failureMessage)
    return this
}

inline fun <T> InteractorPartialState<T>.onError(action: (errorMessage: String) -> Unit): InteractorPartialState<T> {
    if (this is InteractorPartialState.Error) action(errorMessage)
    return this
}

inline fun <T> InteractorPartialState<T>.onNoInternetConnection(action: () -> Unit): InteractorPartialState<T> {
    if (this is InteractorPartialState.NoInternetConnection) action()
    return this
}

inline fun <T> InteractorPartialState<T>.onAnyError(action: (errorMessage: String) -> Unit): InteractorPartialState<T> {
    when (this) {
        is InteractorPartialState.Failed -> action(failureMessage)
        is InteractorPartialState.Error -> action(errorMessage)
        InteractorPartialState.NoInternetConnection -> action("No internet.")
        is InteractorPartialState.Success -> {}
    }
    return this
}