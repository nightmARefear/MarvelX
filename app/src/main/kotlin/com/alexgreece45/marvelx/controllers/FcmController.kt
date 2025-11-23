package com.alexgreece45.marvelx.controllers

import com.alexgreece45.marvelx.util.constants.ConstantsPrefs

/**
 * Firebase cloud messaging controller. Controls the FCM tokens retrieval/storage.
 */
interface FcmController {

    fun setFcmToken(fcmToken: String?)
    fun getFcmToken(): String?
}

class FcmControllerImpl(
    private val preferencesController: PreferencesController
) : FcmController {

    private val key = ConstantsPrefs.FCM_TOKEN

    override fun setFcmToken(fcmToken: String?) {
        if (fcmToken == null) {
            preferencesController.remove(key)
        } else {
            preferencesController.setString(key, fcmToken)
        }
    }

    override fun getFcmToken(): String? = preferencesController.getStringOrNull(key)
}
