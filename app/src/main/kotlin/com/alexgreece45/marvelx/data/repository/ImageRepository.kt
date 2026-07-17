package com.alexgreece45.marvelx.data.repository

import com.alexgreece45.marvelx.core.common.dispatchers.model.IoDispatcher
import com.alexgreece45.marvelx.core.data.helper.RepositoryResult
import com.alexgreece45.marvelx.core.data.helper.handleApiResultNoFlow
import com.alexgreece45.marvelx.core.network.clients.ApiClient
import com.alexgreece45.marvelx.util.extensions.toByteArray
import okhttp3.ResponseBody
import org.koin.core.annotation.Single
import kotlin.coroutines.CoroutineContext

interface ImageRepository {

    /**
     * Retrieves the image from the provided url.
     */
    suspend fun getImage(imageUrl: String): RepositoryResult<ByteArray>
}

@Single
class ImageRepositoryImpl(
    private val apiClient: ApiClient,
    @IoDispatcher private val ioDispatcher: CoroutineContext
) : ImageRepository {

    override suspend fun getImage(imageUrl: String): RepositoryResult<ByteArray> {
        return handleApiResultNoFlow(
            apiResult = apiClient.getImage(imageUrl),
            transformation = ResponseBody::toByteArray,
            ioDispatcher = ioDispatcher
        )
    }
}