package com.alexgreece45.marvelx.core.data.helper

import com.alexgreece45.marvelx.core.common.extensions.safeAsync
import com.alexgreece45.marvelx.core.common.util.withContextCatchable
import com.alexgreece45.marvelx.core.network.model.generic.ApiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.coroutines.CoroutineContext

suspend fun <A : Any, B : Any> handleApiResultNoFlow(
    apiCall: suspend () -> ApiResult<A>,
    transformation: A.() -> B,
    ioDispatcher: CoroutineContext,
    logError: ((String) -> Unit)? = null
): RepositoryResult<B> =
    withContextCatchable<RepositoryResult<B>>(
        context = ioDispatcher,
        onError = { RepositoryResult.Failed(it) },
        logError = logError
    ) {
        when (val apiResult = apiCall()) {
            is ApiResult.Success -> RepositoryResult.Success(transformation(apiResult.body))
            is ApiResult.Error -> RepositoryResult.Failed(apiResult.errorBody)
            is ApiResult.Failed -> RepositoryResult.Failed(apiResult.failureMessage)
            ApiResult.NetworkError -> RepositoryResult.NetworkError
        }
    }

/**
 * Handles the result of an API client call, and returns the appropriate [RepositoryResult]. No data
 * transformation is performed. If the call was successful, [RepositoryResult.Success] is returned
 * with an empty [String] -- the returned value has no use; it's only used to conform to the
 * generics of [RepositoryResult].
 *
 * #### Note: In order for this function to work properly, the generic of [ApiResult] __must__ be [String].
 *
 * @param apiCall The API call.
 * @param ioDispatcher The IO dispatcher.
 */
fun handleApiResultNoTransformation(
    apiCall: suspend () -> ApiResult<String>,
    ioDispatcher: CoroutineContext
): Flow<RepositoryResult<String>> =
    flow {
        when (val apiResult = apiCall()) {
            is ApiResult.Success -> emit(RepositoryResult.Success(apiResult.body))
            is ApiResult.Error -> emit(RepositoryResult.Failed(apiResult.errorBody))
            is ApiResult.Failed -> emit(RepositoryResult.Failed(apiResult.failureMessage))
            ApiResult.NetworkError -> emit(RepositoryResult.NetworkError)
        }
    }.safeAsync(ioDispatcher) {
        RepositoryResult.Failed(it.message.orEmpty())
    }

suspend fun <A : Any> handleApiResultNoTransformationNoFlow(
    apiCall: suspend () -> ApiResult<A>,
    ioDispatcher: CoroutineContext,
    logError: ((String) -> Unit)? = null
): RepositoryResult<A> =
    withContextCatchable<RepositoryResult<A>>(
        context = ioDispatcher,
        onError = { RepositoryResult.Failed(it) },
        logError = logError
    ) {
        when (val apiResult = apiCall()) {
            is ApiResult.Success -> RepositoryResult.Success(apiResult.body)
            is ApiResult.Error -> RepositoryResult.Failed(apiResult.errorBody)
            is ApiResult.Failed -> RepositoryResult.Failed(apiResult.failureMessage)
            ApiResult.NetworkError -> RepositoryResult.NetworkError
        }
    }

/**
 * Handles the result of an API client call, and returns the appropriate [RepositoryResult]. No data
 * transformation is performed.
 * If the call was successful, [RepositoryResult.Success] is returned with the response's data.
 *
 * @param apiCall The API call.
 */
fun <A : Any> handleApiResultNoTransformationWithData(
    apiCall: suspend () -> ApiResult<A>,
    ioDispatcher: CoroutineContext
): Flow<RepositoryResult<A>> =
    flow {
        when (val apiResult = apiCall()) {
            is ApiResult.Success -> emit(RepositoryResult.Success(apiResult.body))
            is ApiResult.Error -> emit(RepositoryResult.Failed(apiResult.errorBody))
            is ApiResult.Failed -> emit(RepositoryResult.Failed(apiResult.failureMessage))
            ApiResult.NetworkError -> emit(RepositoryResult.NetworkError)
        }
    }.safeAsync(ioDispatcher) {
        RepositoryResult.Failed(it.message.orEmpty())
    }