package com.alexgreece45.marvelx.ui.model_generic

sealed class UiState<out T, out R> {
    data object Loading : UiState<Nothing, Nothing>()
    data class Error<R>(val errorData: R) : UiState<Nothing, R>()
    data class Success<T>(val data: T) : UiState<T, Nothing>()

    val isLoading: Boolean get() = this is Loading
}

inline fun <T, R> UiState<T, R>.onLoading(action: () -> Unit): UiState<T, R> {
    if (this is UiState.Loading) action()
    return this
}

inline fun <T, R> UiState<T, R>.onError(action: (R) -> Unit): UiState<T, R> {
    if (this is UiState.Error) action(errorData)
    return this
}

inline fun <T, R> UiState<T, R>.onSuccess(action: (T) -> Unit): UiState<T, R> {
    if (this is UiState.Success) action(data)
    return this
}
