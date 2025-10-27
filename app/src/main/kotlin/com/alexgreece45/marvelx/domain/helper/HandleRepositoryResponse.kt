package com.alexgreece45.marvelx.domain.helper

import com.alexgreece45.marvelx.data.repository.helper.RepositoryResult
import com.alexgreece45.marvelx.network_logic.models_generic.ApiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

/**
 * Handles the given repository [result] and emits the appropriate [InteractorPartialState].
 * [R] denotes the result type, while [D] the domain type -- i.e., the type that will be returned if
 * the data was successfully retrieved.
 *
 * @param result                The repository result.
 * @param transformation        The transformation to apply to [R] in order to transform it into
 * [D].
 */
suspend fun <R : Any, D : Any> handleRepositoryResult(
    result: RepositoryResult<R>,
    transformation: R.() -> D
): Flow<InteractorPartialState<D>> =
    flow {
        when (result) {
            is RepositoryResult.Failed -> emit(InteractorPartialState.Failed(result.failureMessage))
            is RepositoryResult.Success -> emit(InteractorPartialState.Success(transformation(result.data)))
            RepositoryResult.NetworkError -> emit(InteractorPartialState.NoInternetConnection)
        }
    }.catch { InteractorPartialState.Error<D>(it.message.orEmpty()) }

/**
 * Handles the given repository [result] and emits the appropriate [InteractorPartialState]. No data
 * transformation is performed. If the result was successful then [InteractorPartialState.Success]
 * is returned with an empty [String] as the data. The returned value has no use; it's only used to
 * conform to the generics of [InteractorPartialState].
 *
 * #### Note: In order for this function to work properly, the generic of [RepositoryResult] __must__ be [String].
 *
 * @param result    The repository result.
 */
suspend fun handleRepositoryResultNoTransformation(
    result: RepositoryResult<String>,
): Flow<InteractorPartialState<String>> =
    flow {
        when (result) {
            is RepositoryResult.Failed -> emit(InteractorPartialState.Failed(result.failureMessage))
            is RepositoryResult.Success -> emit(InteractorPartialState.Success(result.data))
            RepositoryResult.NetworkError -> emit(InteractorPartialState.NoInternetConnection)
        }
    }.catch { InteractorPartialState.Error<String>(it.message.orEmpty()) }