package com.alexgreece45.marvelx.controllers

import com.alexgreece45.marvelx.core.data.storage.FcmTokenStorage
import org.koin.core.annotation.Factory

/**
 * Firebase cloud messaging controller. Controls the FCM tokens retrieval/storage.
 */
interface FcmController {

    fun getFcmToken(): String?
    fun setFcmToken(fcmToken: String?)
}

@Factory
class FcmControllerImpl(
    private val fcmTokenStorage: FcmTokenStorage
) : FcmController {

    override fun getFcmToken(): String? = fcmTokenStorage.getFcmToken()

    override fun setFcmToken(fcmToken: String?) {
        if (fcmToken == null) {
            fcmTokenStorage.removeFcmToken()
        } else {
            fcmTokenStorage.setFcmToken(fcmToken)
        }
    }
}