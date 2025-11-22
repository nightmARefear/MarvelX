package com.alexgreece45.marvelx.data.repository

import com.alexgreece45.marvelx.data.repository.helper.RepositoryResult
import com.alexgreece45.marvelx.data.repository.helper.handleApiResult
import com.alexgreece45.marvelx.dispatchers.IoDispatcher
import com.alexgreece45.marvelx.network_logic.clients.ApiClient
import com.alexgreece45.marvelx.util.extensions.toByteArray
import kotlinx.coroutines.flow.first
import okhttp3.ResponseBody
import kotlin.coroutines.CoroutineContext

interface ImageRepository {

    /**
     * Retrieves the image from the provided url.
     */
    suspend fun getImage(imageUrl: String): RepositoryResult<ByteArray>
}

class ImageRepositoryImpl(
    private val apiClient: ApiClient,
    @IoDispatcher private val ioDispatcher: CoroutineContext
) : ImageRepository {

    override suspend fun getImage(imageUrl: String): RepositoryResult<ByteArray> {
        return handleApiResult(
            apiResult = apiClient.getImage(imageUrl),
            transformation = ResponseBody::toByteArray,
            ioDispatcher = ioDispatcher
        ).first()
    }
}
