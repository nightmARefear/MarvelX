package com.alexgreece45.marvelx.ui.screens.mainActivity

import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.alexgreece45.marvelx.controllers.AppLogger
import com.alexgreece45.marvelx.controllers.DeepLinkController
import com.alexgreece45.marvelx.controllers.DeviceController
import com.alexgreece45.marvelx.controllers.FcmController
import com.alexgreece45.marvelx.controllers.HandleDeepLinkActionResult
import com.alexgreece45.marvelx.controllers.PrefKeys
import com.alexgreece45.marvelx.controllers.SecurityController
import com.alexgreece45.marvelx.controllers.WebsocketController
import com.alexgreece45.marvelx.controllers.model.security.SecurityViolation
import com.alexgreece45.marvelx.controllers.model.websocket.WebsocketOnMessageReceivedPartialState
import com.alexgreece45.marvelx.controllers.model.websocket.WebsocketRequestType
import com.alexgreece45.marvelx.controllers.model.websocket.WebsocketSendRequestPartialState
import com.alexgreece45.marvelx.data.repository.UserRepository
import com.alexgreece45.marvelx.dispatchers.IoDispatcher
import com.alexgreece45.marvelx.domain.interactors.LoginInteractor
import com.alexgreece45.marvelx.domain.interactors.LogoutPartialState
import com.alexgreece45.marvelx.domain.model_websocket.toWebsocketDomain
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.network_logic.models_websocket.ResponseWebsocket
import com.alexgreece45.marvelx.ui.navigation.router.RouterHost
import com.alexgreece45.marvelx.ui.screens.mainActivity.model.CoroutineWebSocketListener
import com.alexgreece45.marvelx.ui.screens.mainActivity.model.MainActivityEffect
import com.alexgreece45.marvelx.ui.screens.mainActivity.model.MainActivityEffect.Navigation.NavigateToSplashScreenWithSecurityViolation
import com.alexgreece45.marvelx.ui.screens.mainActivity.model.MainActivityEffect.OpenOrCacheDeepLink
import com.alexgreece45.marvelx.ui.screens.mainActivity.model.MainActivityEvent
import com.alexgreece45.marvelx.ui.screens.mainActivity.model.MainActivityState
import com.alexgreece45.marvelx.ui.screens.mainActivity.model.WebSocketEvent
import com.alexgreece45.marvelx.util.DateUtil
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.constants.ConstantsSecurity
import com.alexgreece45.marvelx.util.constants.ConstantsWorker
import com.alexgreece45.marvelx.util.extensions.differenceInMinutes
import com.google.gson.Gson
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import okhttp3.WebSocketListener
import org.koin.core.component.inject
import org.koin.core.qualifier.qualifier
import java.time.Instant
import java.time.format.DateTimeParseException
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class MainActivityViewModel: BaseViewModel<MainActivityEvent, MainActivityState, MainActivityEffect>() {

    override fun setInitialState(): MainActivityState = MainActivityState()

    private val prefKeys: PrefKeys by inject()
    private val logger: AppLogger by inject()
    private val loginInteractor: LoginInteractor by inject()
    private val resourceProvider: ResourceProvider by inject()
    private val websocketController: WebsocketController by inject()
    private val securityController: SecurityController by inject()
    private val routerHost: RouterHost by inject()
    private val deepLinkController: DeepLinkController by inject()
    private val userRepository: UserRepository by inject()
    @IoDispatcher private val ioDispatcher: CoroutineContext by inject(qualifier<IoDispatcher>())
    private val fcmController: FcmController by inject()
    private val deviceController: DeviceController by inject()

    override fun handleEvents(event: MainActivityEvent) {
        when (event) {
            MainActivityEvent.LifecycleEvent.OnCreate -> {
                prefKeys.autoLogoutTimestamp = null
                onCreateOperations()
            }

            MainActivityEvent.LifecycleEvent.OnPause -> {
                if (userRepository.isUserLoggedIn && securityController.isAutoLogoutEnabled()) {
                    prefKeys.autoLogoutTimestamp = DateUtil.getUTCInstant()
                }
            }

            MainActivityEvent.LifecycleEvent.OnResume -> {
                if (userRepository.isUserLoggedIn) { // TODO: To be reviewed.
                    when (prefKeys.shouldRememberUserSession()) {
                        false -> {
                            if (securityController.isAutoLogoutEnabled()) {
                                onLogoutUser()
                            }
                        }

                        true -> performAutoLogoutOperations()
                    }
                }
            }

            is MainActivityEvent.LifecycleEvent.OnDestroy -> {
                if (!event.willBeRecreated) {
                    onDestroy()
                }
            }

            MainActivityEvent.OnUserInteraction -> {
                performAutoLogoutOperations()
            }

            MainActivityEvent.ApplicationDrawOverlayWindowsViolation -> {
                onLogoutUser(showErrorDialog = false)
                setEffect {
                    NavigateToSplashScreenWithSecurityViolation(
                        SecurityViolation.ApplicationDrawOverlayWindows
                    )
                }
            }

            MainActivityEvent.ApplicationInMultiWindowModeViolation -> {
                onLogoutUser(showErrorDialog = false)
                setEffect {
                    NavigateToSplashScreenWithSecurityViolation(
                        SecurityViolation.ApplicationInMultiWindowMode
                    )
                }
            }

            is MainActivityEvent.OnDeepLinkReceived -> {
                viewModelScope.launch {
                    deepLinkController.handleDeepLinkUri(event.uri).collect {
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
    }

    /**
     * The actions to perform when the viewModel is instantiated.
     */
    private fun setWebsocketListener() {
        logger.d { "authData: ${prefKeys.getUserAuthData()}" }
        websocketController.setWebSocketListener(getWebSocketListener())
    }

    private fun registerOrUpdateDevice() {
        viewModelScope.launch {
            deviceController.registerOrUpdateDevice(fcmController.getFcmToken())
                .collect { wasDeviceRegistered ->
                    if (wasDeviceRegistered) {
                        appLogger.e("MainActivityViewModel") { "(registerOrUpdateDevice) An error occurred whilst registering the device." }
                    } else {
                        appLogger.d("MainActivityViewModel") { "(registerOrUpdateDevice) Device was successfully registered!" }
                    }
                }
        }
    }

    /**
     * The actions to perform to logout the user. If you don't want to show the error dialog, then
     * set [showErrorDialog] to `false`.
     *
     * @param showErrorDialog Defines whether the error dialog is shown. It has a default value of
     * `true`.
     */
    private fun onLogoutUser(showErrorDialog: Boolean = true) {
        setEffect {
            MainActivityEffect.Navigation.NavigateToHomeScreenWithSessionExpired(showErrorDialog = showErrorDialog)
        }
        viewModelScope.launch {
            loginInteractor.logout().collect {
                when (it) {
                    is LogoutPartialState.Failed, LogoutPartialState.Success -> {}
                }
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
        prefKeys.autoLogoutTimestamp = null
    }

    /**
     * Performs actions to auto-logout the user if the timeout period has been exceeded.
     */
    private fun performAutoLogoutOperations() {
        if (securityController.isAutoLogoutEnabled() && routerHost.isCurrentRouteIncludedInAutoLogout()) {
            viewModelScope.launch {
                websocketController.sendRequest(WebsocketRequestType.GetTimestamp)
                    .collect { partialState ->
                        val currentTimestamp =
                            when (partialState) {
                                is WebsocketSendRequestPartialState.Failed -> {
                                    logger.d("ActivityViewModel") { "(performAutoLogoutOperations#sendRequest#Failed) errorMessage: ${partialState.errorMessage}" }
                                    DateUtil.getUTCInstant()
                                }

                                is WebsocketSendRequestPartialState.Success -> {
                                    try {
                                        logger.d("ActivityViewModel") { "(performAutoLogoutOperations#sendRequest#Success) timestamp: ${partialState.websocketDomain.data}" }
                                        Instant.parse(partialState.websocketDomain.data)
                                    } catch (e: DateTimeParseException) {
                                        logger.e("ActivityViewModel") { "(performAutoLogoutOperations#sendRequest#Success) parsing error: $e" }
                                        DateUtil.getUTCInstant()
                                    }
                                }
                            }

                        when (val storedTimestamp = prefKeys.autoLogoutTimestamp) {
                            null -> {
                                prefKeys.autoLogoutTimestamp = currentTimestamp
                            }

                            else -> {
                                if (currentTimestamp.differenceInMinutes(storedTimestamp)
                                        .also { logger.d("ActivityViewModel") { "difference in time: $it" } } >= ConstantsSecurity.AutoLogout.TIMEOUT_PERIOD
                                ) {
                                    onLogoutUser()
                                } else {
                                    prefKeys.autoLogoutTimestamp = currentTimestamp
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
                        appLogger.d("ActivityViewModel") { "The websocket connection has been opened." }
                    }

                    is WebSocketEvent.OnMessage -> {
                        appLogger.d("ActivityViewModel") { "Received message: ${webSocketEvent.text}" }

                        try {
                            val websocketDomain =
                                Gson().fromJson(webSocketEvent.text, ResponseWebsocket::class.java)
                                    .toWebsocketDomain()
                            websocketController.onMessageReceived(
                                WebsocketOnMessageReceivedPartialState.Success(websocketDomain)
                            )
                            appLogger.d("ActivityViewModel") { "(onMessage) websocketDomain: $websocketDomain" }
                        } catch (e: Exception) {
                            appLogger.e("ActivityViewModel") { "(onMessage) Error parsing websocket response: $e" }
                            websocketController.onMessageReceived(
                                WebsocketOnMessageReceivedPartialState.Failed(e.message.orEmpty())
                            )
                        }
                    }

                    is WebSocketEvent.OnClosing -> {
                        appLogger.d("ActivityViewModel") { "(onClosing): The websocket server has decided to close the websocket connection.\tcode: ${webSocketEvent.code}" }

                        if (userRepository.isUserLoggedIn) {
                            onLogoutUser()
                        }
                    }

                    is WebSocketEvent.OnClosed -> {
                        if (userRepository.isUserLoggedIn) {
                            appLogger.d("ActivityViewModel") { "(onClosed): isUserLoggedIn: true" }
                            onLogoutUser()
                        }
                        appLogger.d("ActivityViewModel") { "(onClosed): Both the websocket client and server have decided to end this websocket connection.\tcode: ${webSocketEvent.code}" }
                    }

                    is WebSocketEvent.OnFailure -> {
                        appLogger.d("ActivityViewModel") { "(onFailure): The websocket server has decided to close this websocket connection." }
                        onLogoutUser()
                    }
                }
            }
        }
        return listener
    }
}
