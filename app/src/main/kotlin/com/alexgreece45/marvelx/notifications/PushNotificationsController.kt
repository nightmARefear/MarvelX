package com.alexgreece45.marvelx.notifications

import android.app.NotificationManager
import android.content.Context
import androidx.core.content.ContextCompat.getSystemService
import com.alexgreece45.marvelx.controllers.DeviceController
import com.alexgreece45.marvelx.controllers.FcmController
import com.alexgreece45.marvelx.core.common.applicationScope.model.ApplicationScope
import com.alexgreece45.marvelx.core.common.dispatchers.model.IoDispatcher
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import com.alexgreece45.marvelx.data.model.deepLink.DeepLinkAction
import com.alexgreece45.marvelx.data.repository.ImageRepository
import com.alexgreece45.marvelx.data.repository.UserRepository
import com.alexgreece45.marvelx.data.repository.helper.RepositoryResult
import com.alexgreece45.marvelx.notifications.constants.ConstantsNotifications
import com.alexgreece45.marvelx.notifications.model.NotificationChannelType
import com.alexgreece45.marvelx.notifications.model.NotificationData
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single
import kotlin.coroutines.CoroutineContext

@Single
class PushNotificationsController(
    @IoDispatcher ioDispatcher: CoroutineContext,
    @ApplicationScope private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + ioDispatcher),
    private val appLogger: AppLogger,
    private val fcmController: FcmController,
    private val deviceController: DeviceController,
    private val userRepository: UserRepository,
    private val gson: Gson,
    private val imageRepository: ImageRepository,
    private val context: Context
) {

    private val loggerTag: String = "PushNotificationsController"

    private var _notifications: MutableSharedFlow<NotificationData?> = MutableSharedFlow(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    /**
     * The last received notification.
     */
    val notification: SharedFlow<NotificationData?> = _notifications.asSharedFlow()

    /**
     * Updates the FCM token both locally and remotely.
     */
    fun updateFcmToken(token: String) {
        fcmController.setFcmToken(token)
        if (!userRepository.isUserLoggedIn) return

        scope.launch {
            val wasTokenUpdated = deviceController.registerOrUpdateDevice(token)
            if (wasTokenUpdated) {
                appLogger.d(loggerTag) { "(updateToken) FCM Token was successfully updated." }
            } else {
                appLogger.e(loggerTag) { "(updateToken) An error occurred whilst updating the FCM token." }
            }
        }
    }

    /**
     * Sets the incoming notification.
     */
    fun setNotification(
        messageData: Map<String, String>,
        fcmNotification: RemoteMessage.Notification
    ) {
        scope.launch {
            _notifications.emit(parseNotificationData(messageData, fcmNotification))
        }
    }

    /**
     * Parses the data of a notification.
     */
    private suspend fun parseNotificationData(
        messageData: Map<String, String>,
        fcmNotification: RemoteMessage.Notification
    ): NotificationData? {
        return try {
            val imageUrl = messageData[ConstantsNotifications.IMAGE_URL]
            var imageBytes: ByteArray? = null

            if (imageUrl != null) {
                when (val repositoryResult = imageRepository.getImage(imageUrl)) {
                    is RepositoryResult.Failed,
                    RepositoryResult.NetworkError -> {
                        appLogger.e(loggerTag) { "(parseNotificationData) An error occurred whilst retrieving the image." }
                        return null
                    }

                    is RepositoryResult.Success -> {
                        imageBytes = repositoryResult.data
                    }
                }
            }

            NotificationData(
                channelType = NotificationChannelType.getChannelByType(messageData[ConstantsNotifications.CHANNEL_TYPE]!!),
                notificationId = messageData[ConstantsNotifications.NOTIFICATION_ID]!!.toInt(),
                title = fcmNotification.title.orEmpty(),
                body = fcmNotification.body.orEmpty(),
                userId = messageData[ConstantsNotifications.USER_ID]!!,
                imageBytes = imageBytes,
                deepLinkAction = gson.fromJson(
                    messageData[ConstantsNotifications.DEEP_LINK_ACTION]!!,
                    DeepLinkAction::class.java
                )
            )
        } catch (e: Exception) {
            appLogger.e(loggerTag) { "(parseNotificationData) An error occurred whilst parsing the notification: ${e.localizedMessage}" }
            null
        }
    }

    /**
     * Deletes a specific notification.
     */
    fun deleteNotification(notificationId: Int) {
        val notificationManager =
            getSystemService(context, NotificationManager::class.java) as NotificationManager
        notificationManager.cancel(notificationId)
    }
}