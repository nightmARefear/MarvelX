package com.alexgreece45.marvelx.ui.screens.mainActivity

import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.alexgreece45.marvelx.controllers.DeepLinkController
import com.alexgreece45.marvelx.controllers.DeviceController
import com.alexgreece45.marvelx.controllers.FcmController
import com.alexgreece45.marvelx.controllers.HandleDeepLinkActionResult
import com.alexgreece45.marvelx.controllers.WebsocketController
import com.alexgreece45.marvelx.controllers.model.websocket.WebsocketInbound
import com.alexgreece45.marvelx.core.common.config.SecurityConfigLogic
import com.alexgreece45.marvelx.core.common.constants.ConstantsSecurity
import com.alexgreece45.marvelx.core.common.extensions.differenceInMinutes
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import com.alexgreece45.marvelx.core.network.managers.SessionManager
import com.alexgreece45.marvelx.core.network.managers.model.LogoutReason
import com.alexgreece45.marvelx.core.security.model.security.SecurityViolation
import com.alexgreece45.marvelx.data.model.deepLink.DeepLinkAction
import com.alexgreece45.marvelx.data.repository.AutoLogoutRepository
import com.alexgreece45.marvelx.data.repository.UserRepository
import com.alexgreece45.marvelx.domain.usecases.LogoutUseCase
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.notifications.PushNotificationsController
import com.alexgreece45.marvelx.ui.navigation.Screen
import com.alexgreece45.marvelx.ui.screens.mainActivity.model.MainActivityEffect
import com.alexgreece45.marvelx.ui.screens.mainActivity.model.MainActivityEffect.Navigation.NavigateToSplashScreenWithSecurityViolation
import com.alexgreece45.marvelx.ui.screens.mainActivity.model.MainActivityEffect.OpenOrCacheDeepLink
import com.alexgreece45.marvelx.ui.screens.mainActivity.model.MainActivityEvent
import com.alexgreece45.marvelx.ui.screens.mainActivity.model.MainActivityState
import com.alexgreece45.marvelx.util.DateUtil
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.constants.ConstantsWorker
import com.google.gson.Gson
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@KoinViewModel(binds = [MainActivityViewModel::class])
class MainActivityViewModel :
    BaseViewModel<MainActivityEvent, MainActivityState, MainActivityEffect>(), KoinComponent {

    private val loggerTag = "MainActivityViewModel"

    private val appLogger: AppLogger by inject()
    private val securityConfigLogic: SecurityConfigLogic by inject()
    private val resourceProvider: ResourceProvider by inject()
    private val websocketController: WebsocketController by inject()
    private val deepLinkController: DeepLinkController by inject()
    private val userRepository: UserRepository by inject()

    private val fcmController: FcmController by inject()
    private val deviceController: DeviceController by inject()
    private val gson: Gson by inject()
    private val pushNotificationsController: PushNotificationsController by inject()
    private val logoutUseCase: LogoutUseCase by inject()
    private val sessionManager: SessionManager by inject()
    private val autoLogoutRepository: AutoLogoutRepository by inject()

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
                websocketController.onAppForegroundChanged(isForeground = false)

                if (userRepository.isUserLoggedIn && securityConfigLogic.isAutoLogoutEnabled) {
                    viewModelScope.launch {
                        autoLogoutRepository.setAutoLogoutTimestamp(DateUtil.getUTCInstant())
                    }
                }
            }

            MainActivityEvent.LifecycleEvent.OnResume -> {
                websocketController.onAppForegroundChanged(isForeground = true)

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
        viewModelScope.launch {
            userRepository.getUserLoggedInFlow().collect { isUserLoggedIn ->
                if (isUserLoggedIn) {
                    registerOrUpdateDevice()
                    websocketController.connect()
                } else {
                    websocketController.disconnect()
                }
            }
        }

        viewModelScope.launch {
            websocketController.inbound
                .filterIsInstance<WebsocketInbound.NotificationDeleted>()
                .collect { pushNotificationsController.deleteNotification(it.notificationId) }
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
        websocketController.disconnect()

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
                // Prefer the server's clock so a tampered device clock cannot extend the session;
                // fall back to local time if the socket cannot answer in time.
                val currentTimestamp =
                    websocketController.requestServerTimestamp() ?: DateUtil.getUTCInstant()

                when (val storedTimestamp = autoLogoutRepository.getAutoLogoutTimestamp()) {
                    null -> autoLogoutRepository.setAutoLogoutTimestamp(currentTimestamp)

                    else -> {
                        val idleMinutes = currentTimestamp.differenceInMinutes(storedTimestamp)
                        appLogger.d(loggerTag) { "(performAutoLogoutOperations) idle minutes: $idleMinutes" }

                        if (idleMinutes >= ConstantsSecurity.AutoLogout.TIMEOUT_PERIOD) {
                            sessionManager.triggerLogout(LogoutReason.SESSION_EXPIRED)
                        } else {
                            autoLogoutRepository.setAutoLogoutTimestamp(currentTimestamp)
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns `true` if the current route is included in the auto-logout mechanism, otherwise
     * `false`.
     */
    private fun isCurrentRouteIncludedInAutoLogout(): Boolean =
        Screen.fromRoute(currentState.currentRoute)?.isIncludedInAutoLogout ?: false
}