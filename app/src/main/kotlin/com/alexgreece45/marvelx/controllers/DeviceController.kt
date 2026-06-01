package com.alexgreece45.marvelx.controllers

import com.alexgreece45.marvelx.core.common.dispatchers.model.IoDispatcher
import com.alexgreece45.marvelx.core.network.clients.ApiClient
import com.alexgreece45.marvelx.core.network.model.api.device.RequestRegisterDevice
import com.alexgreece45.marvelx.core.network.model.api.device.RequestUpdateDevice
import com.alexgreece45.marvelx.data.repository.helper.RepositoryResult
import com.alexgreece45.marvelx.data.repository.helper.handleApiResultNoTransformationNoFlow
import org.koin.core.annotation.Single
import kotlin.coroutines.CoroutineContext

/**
 * Contains all the utility functions for registering this device.
 */
interface DeviceController {

    /**
     * Registers or updates the device depending on the provided FCM token. If it's different from
     * the saved one then the update operation is performed, otherwise the registration.
     *
     * @param fcmToken The retrieved FCM token.
     */
    suspend fun registerOrUpdateDevice(fcmToken: String?): Boolean
}

@Single
class DeviceControllerImpl(
    private val fcmController: FcmController,
    private val apiClient: ApiClient,
    @IoDispatcher private val ioDispatcher: CoroutineContext,
) : DeviceController {

    override suspend fun registerOrUpdateDevice(fcmToken: String?): Boolean {
        if (fcmToken == null) return false

        val savedFcmToken = fcmController.getFcmToken()
        return if (savedFcmToken != null && savedFcmToken != fcmToken) {
            updateDevice(fcmToken)
        } else {
            registerDevice(fcmToken)
        }
    }

    private suspend fun registerDevice(fcmToken: String?): Boolean {
        return handleApiResultNoTransformationNoFlow(
            apiResult = apiClient.registerDevice(
                RequestRegisterDevice(
                    fcmToken = fcmToken
                )
            ),
            ioDispatcher = ioDispatcher
        ) is RepositoryResult.Success
    }

    private suspend fun updateDevice(fcmToken: String?): Boolean {
        return handleApiResultNoTransformationNoFlow(
            apiResult = apiClient.updateDevice(
                RequestUpdateDevice(
                    fcmToken = fcmToken
                )
            ),
            ioDispatcher = ioDispatcher
        ) is RepositoryResult.Success
    }
}
