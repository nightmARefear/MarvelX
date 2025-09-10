package com.alexgreece45.marvelx.data.repository.helper

import com.alexgreece45.marvelx.network_logic.models_generic.ApiResult
import com.alexgreece45.marvelx.util.extensions.safeAsync
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Handles the result of an API client call, and returns the appropriate [RepositoryResult] by
 * performing data transformation if the call was successful.
 *
 *
 * [A] denotes the API model type, while [B] the repository one. To transform [A] to [B],
 * [transformation] is used.
 *
 * @param apiResult         The API result.
 * @param transformation    The transformation to apply to [A] in order to transform it into [B].
 */
internal suspend fun <A : Any, B : Any> handleApiResult(
    apiResult: ApiResult<A>,
    transformation: A.() -> B
): Flow<RepositoryResult<B>> =
    flow {
        when (apiResult) {
            is ApiResult.Success -> emit(RepositoryResult.Success(transformation(apiResult.body)))
            is ApiResult.Error -> emit(RepositoryResult.Failed(apiResult.errorBody))
            is ApiResult.Failed -> emit(RepositoryResult.Failed(apiResult.failureMessage))
            ApiResult.NetworkError -> emit(RepositoryResult.NetworkError)
        }
    }.safeAsync {
        RepositoryResult.Failed(it.message.orEmpty())
    }

/**
 * Handles the result of an API client call, and returns the appropriate [RepositoryResult]. No data
 * transformation is performed. If the call was successful, [RepositoryResult.Success] is returned
 * with an empty [String] -- the returned value has no use; it's only used to conform to the
 * generics of [RepositoryResult].
 *
 * #### Note: In order for this function to work properly, the generic of [ApiResult] __must__ be [String].
 *
 * @param apiResult The API result.
 */
internal suspend fun handleApiResultNoTransformation(
    apiResult: ApiResult<String>
): Flow<RepositoryResult<String>> =
    flow {
        when (apiResult) {
            is ApiResult.Success -> emit(RepositoryResult.Success(apiResult.body))
            is ApiResult.Error -> emit(RepositoryResult.Failed(apiResult.errorBody))
            is ApiResult.Failed -> emit(RepositoryResult.Failed(apiResult.failureMessage))
            ApiResult.NetworkError -> emit(RepositoryResult.NetworkError)
        }
    }.safeAsync {
        RepositoryResult.Failed(it.message.orEmpty())
    }

/**
 * Handles the result of an API client call, and returns the appropriate [RepositoryResult]. No data
 * transformation is performed.
 * If the call was successful, [RepositoryResult.Success] is returned with the response's data.
 *
 * @param apiResult The API result.
 */
internal suspend fun <A : Any> handleApiResultNoTransformationWithData(
    apiResult: ApiResult<A>,
): Flow<RepositoryResult<A>> =
    flow {
        when (apiResult) {
            is ApiResult.Success -> emit(RepositoryResult.Success(apiResult.body))
            is ApiResult.Error -> emit(RepositoryResult.Failed(apiResult.errorBody))
            is ApiResult.Failed -> emit(RepositoryResult.Failed(apiResult.failureMessage))
            ApiResult.NetworkError -> emit(RepositoryResult.NetworkError)
        }
    }.safeAsync {
        RepositoryResult.Failed(it.message.orEmpty())
    }
