package com.alexgreece45.marvelx.core.network.util

import com.alexgreece45.marvelx.core.network.model.generic.ApiResult
import okio.IOException
import retrofit2.Response

/**
 * Executes the given API service call and returns the appropriate [ApiResult]. [T] denotes the data
 * type of a successful API call.
 */
internal suspend fun <T : Any> executeApiServiceCall(apiCall: suspend () -> Response<T>): ApiResult<T> {
    return try {
        val response = apiCall()
        if (response.isSuccessful) {
            when (val data = response.body()) {
                null -> ApiResult.Failed(response.errorBody().toString())
                else -> ApiResult.Success(data)
            }
        } else {
            ApiResult.Failed(response.errorBody().toString())
        }
    } catch (_: IOException) {
        ApiResult.NetworkError
    } catch (e: Exception) {
        ApiResult.Error(e.localizedMessage.orEmpty())
    }
}

/**
 * Executes the given API service call and returns the appropriate [ApiResult]. This function is
 * used when we don't wish to perform data transformation on the returned data.
 *
 * If the call was successful then [ApiResult.Success] with an empty [String] is returned. The
 * returned value has no use as it's just a placeholder to conform to the generics of [ApiResult].
 *
 * #### Note: For this function to work properly, the generic type of [apiCall] __must__ be [Void].
 */
internal suspend fun executeApiServiceCallNoTransformation(apiCall: suspend () -> Response<Void>): ApiResult<String> {
    return try {
        val response = apiCall()
        if (response.isSuccessful) {
            ApiResult.Success("")
        } else {
            ApiResult.Failed(response.errorBody().toString())
        }
    } catch (_: IOException) {
        ApiResult.NetworkError
    } catch (e: Exception) {
        ApiResult.Error(e.localizedMessage.orEmpty())
    }
}