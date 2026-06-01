package com.alexgreece45.marvelx.notifications.firebaseService

import com.alexgreece45.marvelx.notifications.PushNotificationsController
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.koin.android.ext.android.inject

class MarvelXFirebaseService : FirebaseMessagingService() {

    private val pushNotificationsController: PushNotificationsController by inject()

    override fun onMessageReceived(message: RemoteMessage) {
        val fcmNotification = message.notification
        if (message.data.isEmpty() || fcmNotification == null) return

        pushNotificationsController.setNotification(message.data, fcmNotification)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        pushNotificationsController.updateFcmToken(token)
    }
}
