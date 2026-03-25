package com.alexgreece45.marvelx.domain.helper

import com.alexgreece45.marvelx.data.repository.helper.RepositoryResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

/**
 * Handles the given repository [repositoryResult] and emits the appropriate [InteractorPartialState].
 * [R] denotes the result type, while [D] the domain type -- i.e., the type that will be returned if
 * the data was successfully retrieved.
 *
 * @param repositoryResult      The repository result.
 * @param transformation        The transformation to apply to [R] in order to transform it into
 * [D].
 */
fun <R : Any, D : Any> handleRepositoryResult(
    repositoryResult: RepositoryResult<R>,
    transformation: R.() -> D
): Flow<InteractorPartialState<D>> =
    flow {
        when (repositoryResult) {
            is RepositoryResult.Failed -> emit(InteractorPartialState.Failed(repositoryResult.failureMessage))
            is RepositoryResult.Success -> emit(
                InteractorPartialState.Success(
                    transformation(
                        repositoryResult.data
                    )
                )
            )

            RepositoryResult.NetworkError -> emit(InteractorPartialState.NoInternetConnection)
        }
    }.catch { InteractorPartialState.Error<D>(it.message.orEmpty()) }


/**
 * Handles the given repository [repositoryResult] and returns the appropriate [InteractorPartialState].
 * [R] denotes the result type, while [D] the domain type -- i.e., the type that will be returned if
 * the data was successfully retrieved.
 *
 * @param repositoryResult      The repository result.
 * @param transformation        The transformation to apply to [R] in order to transform it into
 * [D].
 */
fun <R : Any, D : Any> handleRepositoryResultNoFlow(
    repositoryResult: RepositoryResult<R>,
    transformation: R.() -> D
): InteractorPartialState<D> =
    try {
        when (repositoryResult) {
            is RepositoryResult.Failed -> InteractorPartialState.Failed(repositoryResult.failureMessage)
            is RepositoryResult.Success -> InteractorPartialState.Success(
                transformation(
                    repositoryResult.data
                )
            )

            RepositoryResult.NetworkError -> InteractorPartialState.NoInternetConnection
        }
    } catch (e: Exception) {
        InteractorPartialState.Error(e.message.orEmpty())
    }

/**
 * Handles the given repository [repositoryResult] and emits the appropriate [InteractorPartialState].
 * No data transformation is performed. If the result was successful then
 * [InteractorPartialState.Success] is returned with an empty [String] as the data.
 * The returned value has no use; it's only used to conform to the generics of [InteractorPartialState].
 *
 * #### Note: In order for this function to work properly, the generic of [RepositoryResult] __must__ be [String].
 *
 * @param repositoryResult    The repository result.
 */
fun handleRepositoryResultNoTransformation(
    repositoryResult: RepositoryResult<String>,
): Flow<InteractorPartialState<String>> =
    flow {
        when (repositoryResult) {
            is RepositoryResult.Failed -> emit(InteractorPartialState.Failed(repositoryResult.failureMessage))
            is RepositoryResult.Success -> emit(InteractorPartialState.Success(repositoryResult.data))
            RepositoryResult.NetworkError -> emit(InteractorPartialState.NoInternetConnection)
        }
    }.catch { InteractorPartialState.Error<String>(it.message.orEmpty()) }

/**
 * Handles the given repository [repositoryResult] and returns the appropriate [InteractorPartialState].
 * No data transformation is performed. If the result was successful then
 * [InteractorPartialState.Success] is returned with an empty [String] as the data.
 * The returned value has no use; it's only used to conform to the generics of [InteractorPartialState].
 *
 * #### Note: In order for this function to work properly, the generic of [RepositoryResult] __must__ be [String].
 *
 * @param repositoryResult    The repository result.
 */
fun handleRepositoryResultNoTransformationNoFlow(
    repositoryResult: RepositoryResult<String>,
): InteractorPartialState<String> =
    try {
        when (repositoryResult) {
            is RepositoryResult.Failed -> InteractorPartialState.Failed(repositoryResult.failureMessage)
            is RepositoryResult.Success -> InteractorPartialState.Success(
                repositoryResult.data
            )

            RepositoryResult.NetworkError -> InteractorPartialState.NoInternetConnection
        }
    } catch (e: Exception) {
        InteractorPartialState.Error(e.message.orEmpty())
    }

/**
 * Handles the given repository [repositoryResult] and returns the appropriate [InteractorPartialState].
 * No data transformation is performed. If the result was successful then
 * [InteractorPartialState.Success] is returned with the appropriate data.
 *
 * @param repositoryResult The repository result.
 */
fun <T : Any> handleRepositoryResultNoTransformationNoFlowWithData(
    repositoryResult: RepositoryResult<T>,
): InteractorPartialState<T> =
    try {
        when (repositoryResult) {
            is RepositoryResult.Failed -> InteractorPartialState.Failed(repositoryResult.failureMessage)
            is RepositoryResult.Success -> InteractorPartialState.Success(
                repositoryResult.data
            )

            RepositoryResult.NetworkError -> InteractorPartialState.NoInternetConnection
        }
    } catch (e: Exception) {
        InteractorPartialState.Error(e.message.orEmpty())
    }
