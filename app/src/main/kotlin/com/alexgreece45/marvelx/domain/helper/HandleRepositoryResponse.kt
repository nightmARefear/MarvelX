package com.alexgreece45.marvelx.domain.helper

import com.alexgreece45.marvelx.data.repository.helper.RepositoryPartialState
import com.alexgreece45.marvelx.util.extensions.safeAsync
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException

/**
 * Handles the given repository [response] and emits the appropriate [InteractorPartialState].
 * [R] denotes the response type, while [D] the domain type -- i.e., the type that will be
 * returned if the data was successfully retrieved.
 *
 * @param response              The repository response.
 * @param transformation        The transformation to apply to [R] in order to transform it into
 * [D].
 * @param genericErrorMessage   The generic error message to set when the response body is `null`.
 * It has the default value of an empty [String].
 */
suspend fun <R : Any, D : Any> handleRepositoryResponse(
    response: Flow<RepositoryPartialState<R>>,
    transformation: R.() -> D,
    genericErrorMessage: String = ""
): Flow<InteractorPartialState<D>> =
    flow {
        response.collect { repositoryPartialState ->
            when (repositoryPartialState) {
                is RepositoryPartialState.Failed -> emit(InteractorPartialState.Failed<D>(repositoryPartialState.errorMessage))
                is RepositoryPartialState.Success -> {
                    when (val data = repositoryPartialState.data) {
                        null -> emit(InteractorPartialState.Failed<D>(genericErrorMessage))
                        else -> emit(InteractorPartialState.Success<D>(transformation(data)))
                    }
                }
            }
        }

    }.safeAsync {
        when (it) {
            is IOException -> InteractorPartialState.NoInternetConnection
            else -> InteractorPartialState.Error(it.message.orEmpty())
        }
    }