package com.alexgreece45.marvelx.controllers

import com.alexgreece45.marvelx.data.repository.helper.RepositoryResult
import com.alexgreece45.marvelx.data.repository.helper.handleApiResultNoTransformation
import com.alexgreece45.marvelx.dispatchers.IoDispatcher
import com.alexgreece45.marvelx.network_logic.clients.ApiClient
import com.alexgreece45.marvelx.network_logic.models_api.device.RequestRegisterDevice
import com.alexgreece45.marvelx.network_logic.models_api.device.RequestUpdateDevice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
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
    suspend fun registerOrUpdateDevice(fcmToken: String?): Flow<Boolean>
}

class DeviceControllerImpl(
    private val fcmController: FcmController,
    private val apiClient: ApiClient,
    @IoDispatcher private val ioDispatcher: CoroutineContext,
) : DeviceController {

    override suspend fun registerOrUpdateDevice(fcmToken: String?): Flow<Boolean> = flow {
        if (fcmToken == null) return@flow emit(false)

        val savedFcmToken = fcmController.getFcmToken()
        if (savedFcmToken != null && savedFcmToken != fcmToken) {
            emitAll(updateDevice(fcmToken))
        } else {
            emitAll(registerDevice(fcmToken))
        }
    }

    private fun registerDevice(fcmToken: String?): Flow<Boolean> = flow {
        handleApiResultNoTransformation(
            apiResult = apiClient.registerDevice(
                RequestRegisterDevice(
                    fcmToken = fcmToken
                )
            ),
            ioDispatcher = ioDispatcher
        ).collect { result ->
            emit(result is RepositoryResult.Success)
        }
    }

    private fun updateDevice(fcmToken: String?): Flow<Boolean> = flow {
        handleApiResultNoTransformation(
            apiResult = apiClient.updateDevice(
                RequestUpdateDevice(
                    fcmToken = fcmToken
                )
            ),
            ioDispatcher = ioDispatcher
        ).collect { result ->
            emit(result is RepositoryResult.Success)
        }
    }
}
