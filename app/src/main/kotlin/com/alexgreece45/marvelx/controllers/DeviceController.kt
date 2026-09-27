package com.alexgreece45.marvelx.controllers

import com.alexgreece45.marvelx.core.common.dispatchers.model.IoDispatcher
import com.alexgreece45.marvelx.core.data.helper.RepositoryResult
import com.alexgreece45.marvelx.core.data.helper.handleApiResultNoTransformationNoFlow
import com.alexgreece45.marvelx.core.network.clients.ApiClient
import com.alexgreece45.marvelx.core.network.model.api.device.RequestRegisterDevice
import org.koin.core.annotation.Single
import kotlin.coroutines.CoroutineContext

/**
 * Contains all the utility functions for registering this device.
 */
interface DeviceController {

    /**
     * Registers the device with the provided FCM token. The backend upserts the current user's
     * device, so this is used both for the first registration and for token refreshes.
     *
     * @param fcmToken The retrieved FCM token.
     */
    suspend fun registerDevice(fcmToken: String?): Boolean
}

@Single
class DeviceControllerImpl(
    private val apiClient: ApiClient,
    @IoDispatcher private val ioDispatcher: CoroutineContext,
) : DeviceController {

    override suspend fun registerDevice(fcmToken: String?): Boolean {
        if (fcmToken == null) return false

        return handleApiResultNoTransformationNoFlow(
            apiCall = {
                apiClient.registerDevice(
                    RequestRegisterDevice(
                        fcmToken = fcmToken
                    )
                )
            },
            ioDispatcher = ioDispatcher
        ) is RepositoryResult.Success
    }
}
