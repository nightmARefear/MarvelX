package com.alexgreece45.marvelx.ui.screens.mainActivity

import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.alexgreece45.marvelx.controllers.DeepLinkController
import com.alexgreece45.marvelx.controllers.DeviceController
import com.alexgreece45.marvelx.controllers.FcmController
import com.alexgreece45.marvelx.controllers.HandleDeepLinkActionResult
import com.alexgreece45.marvelx.controllers.WebsocketController
import com.alexgreece45.marvelx.controllers.model.websocket.WebsocketOnMessageReceivedPartialState
import com.alexgreece45.marvelx.controllers.model.websocket.WebsocketRequestType
import com.alexgreece45.marvelx.controllers.model.websocket.WebsocketSendRequestPartialState
import com.alexgreece45.marvelx.core.common.config.SecurityConfigLogic
import com.alexgreece45.marvelx.core.common.constants.ConstantsSecurity
import com.alexgreece45.marvelx.core.common.dispatchers.model.DispatcherQualifiers
import com.alexgreece45.marvelx.core.common.extensions.differenceInMinutes
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import com.alexgreece45.marvelx.core.network.managers.SessionManager
import com.alexgreece45.marvelx.core.network.managers.model.LogoutReason
import com.alexgreece45.marvelx.core.network.model.websocket.ResponseWebSocketDeleteNotification
import com.alexgreece45.marvelx.core.network.model.websocket.ResponseWebsocket
import com.alexgreece45.marvelx.core.security.model.security.SecurityViolation
import com.alexgreece45.marvelx.data.model.deepLink.DeepLinkAction
import com.alexgreece45.marvelx.data.repository.AutoLogoutRepository
import com.alexgreece45.marvelx.data.repository.UserRepository
import com.alexgreece45.marvelx.data.repository.marketplace.listing.ListingCategoryRepository
import com.alexgreece45.marvelx.data.repository.marketplace.listing.details.ListingDetailsRepository
import com.alexgreece45.marvelx.domain.model_websocket.toWebsocketDomain
import com.alexgreece45.marvelx.domain.usecases.LogoutUseCase
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.notifications.PushNotificationsController
import com.alexgreece45.marvelx.ui.navigation.Screen
import com.alexgreece45.marvelx.ui.screens.mainActivity.model.CoroutineWebSocketListener
import com.alexgreece45.marvelx.ui.screens.mainActivity.model.MainActivityEffect
import com.alexgreece45.marvelx.ui.screens.mainActivity.model.MainActivityEffect.Navigation.NavigateToSplashScreenWithSecurityViolation
import com.alexgreece45.marvelx.ui.screens.mainActivity.model.MainActivityEffect.OpenOrCacheDeepLink
import com.alexgreece45.marvelx.ui.screens.mainActivity.model.MainActivityEvent
import com.alexgreece45.marvelx.ui.screens.mainActivity.model.MainActivityState
import com.alexgreece45.marvelx.ui.screens.mainActivity.model.WebSocketEvent
import com.alexgreece45.marvelx.util.DateUtil
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.constants.ConstantsWorker
import com.google.gson.Gson
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import okhttp3.WebSocketListener
import org.koin.android.annotation.KoinViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import java.time.Instant
import java.time.format.DateTimeParseException
import kotlin.coroutines.CoroutineContext

@KoinViewModel
class MainActivityViewModel :
    BaseViewModel<MainActivityEvent, MainActivityState, MainActivityEffect>(), KoinComponent {

    private val loggerTag = "MainActivityViewModel"

    private val appLogger: AppLogger by inject()
    private val securityConfigLogic: SecurityConfigLogic by inject()
    private val resourceProvider: ResourceProvider by inject()
    private val websocketController: WebsocketController by inject()
    private val deepLinkController: DeepLinkController by inject()
    private val userRepository: UserRepository by inject()

    private val ioDispatcher: CoroutineContext by inject(named(DispatcherQualifiers.IO.qualifier))
    private val fcmController: FcmController by inject()
    private val deviceController: DeviceController by inject()
    private val gson: Gson by inject()
    private val pushNotificationsController: PushNotificationsController by inject()
    private val logoutUseCase: LogoutUseCase by inject()
    private val sessionManager: SessionManager by inject()
    private val autoLogoutRepository: AutoLogoutRepository by inject()

    // Listing enum-related values.
    private val listingCategoryRepository: ListingCategoryRepository by inject()
    private val listingDetailsRepository: ListingDetailsRepository by inject()

    override fun setInitialState(): MainActivityState = MainActivityState()

    override fun handleEvents(event: MainActivityEvent) {
        when (event) {
            MainActivityEvent.LifecycleEvent.OnCreate -> {
                viewModelScope.launch {
                    autoLogoutRepository.removeAutoLogoutTimestamp()
                }
                onCreateOperations()
            }

            MainActivityEvent.LifecycleEvent.OnPause -> {
                if (userRepository.isUserLoggedIn && securityConfigLogic.isAutoLogoutEnabled) {
                    viewModelScope.launch {
                        autoLogoutRepository.setAutoLogoutTimestamp(DateUtil.getUTCInstant())
                    }
                }
            }

            MainActivityEvent.LifecycleEvent.OnResume -> {
                if (!userRepository.isUserLoggedIn) return // TODO: To be reviewed.

                viewModelScope.launch {
                    if (autoLogoutRepository.getRememberUserSession()) {
                        performAutoLogoutOperations()
                    } else {
                        if (securityConfigLogic.isAutoLogoutEnabled) {
                            sessionManager.triggerLogout(LogoutReason.SESSION_EXPIRED)
                        }
                    }
                }
            }

            is MainActivityEvent.LifecycleEvent.OnDestroy -> {
                if (!event.willBeRecreated) {
                    onDestroy()
                }
            }

            MainActivityEvent.OnUserInteraction -> {
                if (!userRepository.isUserLoggedIn) return

                viewModelScope.launch {
                    if (autoLogoutRepository.getRememberUserSession()) {
                        performAutoLogoutOperations()
                    }
                }
            }

            MainActivityEvent.ApplicationDrawOverlayWindowsViolation -> {
                sessionManager.triggerLogout(LogoutReason.SECURITY_VALIDATION_ERROR)
                setEffect {
                    NavigateToSplashScreenWithSecurityViolation(
                        SecurityViolation.ApplicationDrawOverlayWindows
                    )
                }
            }

            MainActivityEvent.ApplicationInMultiWindowModeViolation -> {
                sessionManager.triggerLogout(LogoutReason.SECURITY_VALIDATION_ERROR)
                setEffect {
                    NavigateToSplashScreenWithSecurityViolation(
                        SecurityViolation.ApplicationInMultiWindowMode
                    )
                }
            }

            MainActivityEvent.OnFlowStarted -> {
                setState {
                    copy(
                        flowStarted = true
                    )
                }
            }

            is MainActivityEvent.OnDestinationChanged -> {
                setState {
                    copy(
                        currentRoute = event.route
                    )
                }
            }

            is MainActivityEvent.OnDeepLinkReceived -> {
                viewModelScope.launch {
                    deepLinkController.handleDeepLinkUri(event.uri).let {
                        when (it) {
                            is HandleDeepLinkActionResult.Success -> {
                                setEffect {
                                    OpenOrCacheDeepLink(it.deepLinkAction)
                                }
                            }

                            HandleDeepLinkActionResult.Failed -> {}
                        }
                    }
                }
            }

            is MainActivityEvent.OnFcmTokenReceived -> {
                fcmController.setFcmToken(event.fcmToken)

                if (userRepository.isUserLoggedIn) {
                    registerOrUpdateDevice()
                }
            }

            is MainActivityEvent.OnFcmDeepLinkActionReceived -> {
                try {
                    val deepLinkAction =
                        gson.fromJson(
                            event.stringifiedFcmDeepLinkAction,
                            DeepLinkAction::class.java
                        )

                    setEffect {
                        OpenOrCacheDeepLink(deepLinkAction)
                    }
                } catch (e: Exception) {
                    appLogger.e(loggerTag) { "(OnFcmDeepLinkActionReceived) An error occurred whilst parsing the fcm deeplink action.\n ${e.localizedMessage}" }
                }
            }
        }
    }

    /**
     * The operations to perform when the app gets initialized.
     */
    private fun onCreateOperations() {
        setWebsocketListener()

        viewModelScope.launch {
            userRepository.getUserLoggedInFlow().collect { isUserLoggedIn ->
                if (isUserLoggedIn) {
                    registerOrUpdateDevice()
                }
            }
        }

        viewModelScope.launch {
            sessionManager.logoutEvents.collect { logoutReason ->
                setEffect {
                    MainActivityEffect.Navigation.NavigateToHomeScreen(logoutReason)
                }
                logoutUseCase()
            }
        }

        viewModelScope.launch {
            pushNotificationsController.notification
                .filterNotNull()
                .collect { notification ->
                    setEffect {
                        MainActivityEffect.ShowNotification(
                            notificationData = notification
                        )
                    }
                }
        }

        viewModelScope.launch {
            // Initialize listing enum related values.
            listingCategoryRepository.init()
            listingDetailsRepository.init()
        }
    }

    /**
     * The actions to perform when the viewModel is instantiated.
     */
    private fun setWebsocketListener() {
        websocketController.setWebSocketListener(getWebSocketListener())
    }

    private fun registerOrUpdateDevice() {
        viewModelScope.launch {
            val wasDeviceRegistered =
                deviceController.registerOrUpdateDevice(fcmController.getFcmToken())

            if (wasDeviceRegistered) {
                appLogger.d(loggerTag) { "(registerOrUpdateDevice) Device was successfully registered!" }
            } else {
                appLogger.e(loggerTag) { "(registerOrUpdateDevice) An error occurred whilst registering the device." }
            }
        }
    }

    /**
     * The actions to perform when main activity's onDestroy function is called.
     */
    private fun onDestroy() {
        // Cancel refresh token worker.
        WorkManager.getInstance(resourceProvider.getContext())
            .cancelUniqueWork(ConstantsWorker.RefreshToken.WORKER_NAME)

        // Close websocket.
        websocketController.close()

        // Remove auto-logout timestamp.
        viewModelScope.launch {
            autoLogoutRepository.removeAutoLogoutTimestamp()
        }
    }

    /**
     * Performs actions to auto-logout the user if the timeout period has been exceeded.
     */
    private fun performAutoLogoutOperations() {
        if (securityConfigLogic.isAutoLogoutEnabled && isCurrentRouteIncludedInAutoLogout()) {
            viewModelScope.launch {
                websocketController.sendRequest(WebsocketRequestType.GetTimestamp)
                    .collect { partialState ->
                        var logoutReason = LogoutReason.SESSION_EXPIRED
                        val currentTimestamp =
                            when (partialState) {
                                is WebsocketSendRequestPartialState.Failed -> {
                                    appLogger.d(loggerTag) { "(performAutoLogoutOperations#sendRequest#Failed) errorMessage: ${partialState.errorMessage}" }
                                    logoutReason = LogoutReason.WEBSOCKET_ERROR
                                    DateUtil.getUTCInstant()
                                }

                                is WebsocketSendRequestPartialState.Success -> {
                                    try {
                                        appLogger.d(loggerTag) { "(performAutoLogoutOperations#sendRequest#Success) timestamp: ${partialState.websocketDomain.data}" }
                                        Instant.parse(partialState.websocketDomain.data)
                                    } catch (e: DateTimeParseException) {
                                        appLogger.e(loggerTag) { "(performAutoLogoutOperations#sendRequest#Success) parsing error: $e" }
                                        logoutReason = LogoutReason.WEBSOCKET_ERROR
                                        DateUtil.getUTCInstant()
                                    }
                                }
                            }

                        viewModelScope.launch {
                            when (val storedTimestamp =
                                autoLogoutRepository.getAutoLogoutTimestamp()) {
                                null -> {
                                    autoLogoutRepository.setAutoLogoutTimestamp(currentTimestamp)
                                }

                                else -> {
                                    if (currentTimestamp.differenceInMinutes(storedTimestamp)
                                            .also { appLogger.d(loggerTag) { "difference in time: $it" } } >= ConstantsSecurity.AutoLogout.TIMEOUT_PERIOD
                                    ) {
                                        sessionManager.triggerLogout(logoutReason)
                                    } else {
                                        autoLogoutRepository.setAutoLogoutTimestamp(currentTimestamp)
                                    }
                                }
                            }
                        }
                    }
            }
        }
    }

    /**
     * Returns the websocket listener responsible for handling connections, terminations, and more.
     */
    private fun getWebSocketListener(): WebSocketListener {
        val listener = CoroutineWebSocketListener()

        viewModelScope.launch(ioDispatcher) {
            listener.eventChannel.consumeEach { webSocketEvent ->
                when (webSocketEvent) {
                    is WebSocketEvent.OnOpen -> {
                        appLogger.d(loggerTag) { "The websocket connection has been opened." }
                    }

                    is WebSocketEvent.OnMessage -> {
                        appLogger.d(loggerTag) { "Received message: ${webSocketEvent.text}" }

                        try {
                            // Delete notification handling.
                            val notificationId = gson.fromJson(
                                webSocketEvent.text,
                                ResponseWebSocketDeleteNotification::class.java
                            ).notificationId
                            pushNotificationsController.deleteNotification(notificationId)
                        } catch (_: Exception) {
                            appLogger.e(loggerTag) { "(onMessage) The received websocket response is not of \"delete notification\" type." }

                            try {
                                val websocketDomain =
                                    gson.fromJson(
                                        webSocketEvent.text,
                                        ResponseWebsocket::class.java
                                    )
                                        .toWebsocketDomain()
                                websocketController.onMessageReceived(
                                    WebsocketOnMessageReceivedPartialState.Success(websocketDomain)
                                )
                                appLogger.d(loggerTag) { "(onMessage) websocketDomain: $websocketDomain" }
                            } catch (e: Exception) {
                                appLogger.e(loggerTag) { "(onMessage) Error parsing websocket response: $e" }
                                websocketController.onMessageReceived(
                                    WebsocketOnMessageReceivedPartialState.Failed(e.message.orEmpty())
                                )
                            }
                        }
                    }

                    is WebSocketEvent.OnClosing -> {
                        appLogger.d(loggerTag) { "(onClosing): The websocket server has decided to close the websocket connection.\tcode: ${webSocketEvent.code}" }

                        if (userRepository.isUserLoggedIn) {
                            sessionManager.triggerLogout(LogoutReason.WEBSOCKET_ERROR)
                        }
                    }

                    is WebSocketEvent.OnClosed -> {
                        if (userRepository.isUserLoggedIn) {
                            appLogger.d(loggerTag) { "(onClosed): isUserLoggedIn: true" }
                            sessionManager.triggerLogout(LogoutReason.WEBSOCKET_ERROR)
                        }
                        appLogger.d(loggerTag) { "(onClosed): Both the websocket client and server have decided to end this websocket connection.\tcode: ${webSocketEvent.code}" }
                    }

                    is WebSocketEvent.OnFailure -> {
                        appLogger.d(loggerTag) { "(onFailure): The websocket server has decided to close this websocket connection." }
                        sessionManager.triggerLogout(LogoutReason.WEBSOCKET_ERROR)
                    }
                }
            }
        }
        return listener
    }

    /**
     * Returns `true` if the current route is included in the auto-logout mechanism, otherwise
     * `false`.
     */
    private fun isCurrentRouteIncludedInAutoLogout(): Boolean =
        Screen.fromRoute(currentState.currentRoute)?.isIncludedInAutoLogout ?: false
}