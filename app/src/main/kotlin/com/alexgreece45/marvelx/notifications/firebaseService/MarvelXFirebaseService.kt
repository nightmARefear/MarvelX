package com.alexgreece45.marvelx.notifications.firebaseService

import com.alexgreece45.marvelx.notifications.PushNotificationsController
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MarvelXFirebaseService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        val fcmNotification = message.notification
        if (message.data.isEmpty() || fcmNotification == null) return

        PushNotificationsController.setNotification(message.data, fcmNotification)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        PushNotificationsController.updateFcmToken(token)
    }
}
