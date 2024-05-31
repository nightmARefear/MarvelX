package com.alexgreece45.marvelx.ui.screens.mainActivity

import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.alexgreece45.marvelx.controllers.AppLoggerController
import com.alexgreece45.marvelx.controllers.PrefKeys
import com.alexgreece45.marvelx.controllers.SecurityController
import com.alexgreece45.marvelx.controllers.WebsocketController
import com.alexgreece45.marvelx.controllers.model.websocket.WebsocketOnMessageReceivedPartialState
import com.alexgreece45.marvelx.controllers.model.websocket.WebsocketRequestType
import com.alexgreece45.marvelx.controllers.model.websocket.WebsocketSendRequestPartialState
import com.alexgreece45.marvelx.network_logic.models_websocket.ResponseWebsocket
import com.alexgreece45.marvelx.domain.interactors.LoginInteractor
import com.alexgreece45.marvelx.domain.interactors.LogoutPartialState
import com.alexgreece45.marvelx.domain.models_websocket.toWebsocketDomain
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.navigation.router.RouterHost
import com.alexgreece45.marvelx.util.DateUtil
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.constants.ConstantsSecurity
import com.alexgreece45.marvelx.util.constants.ConstantsWorker
import com.alexgreece45.marvelx.util.extensions.differenceInMinutes
import com.alexgreece45.marvelx.ui.screens.mainActivity.models.CoroutineWebSocketListener
import com.alexgreece45.marvelx.ui.screens.mainActivity.models.WebSocketEvent
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import okhttp3.WebSocketListener
import org.koin.core.component.inject
import java.time.Instant
import java.time.format.DateTimeParseException

sealed class ActivityEvent : BaseEvent {
    /**
     * Events triggered by the application lifecycle.
     */
    sealed class LifecycleEvent : ActivityEvent() {
        data object OnCreate : LifecycleEvent()
        data object OnPause : LifecycleEvent()
        data object OnResume : LifecycleEvent()
        data object OnDestroy : LifecycleEvent()
    }

    /**
     * This event is triggered every time the user interacts within the application view.
     */
    data object OnUserInteraction : ActivityEvent()

    /**
     * This event is triggered when this application shouldn't have the ability to draw itself as an
     * overlay window over other applications, but in fact it does.
     */
    data object ApplicationDrawOverlayWindowsViolation : ActivityEvent()

    /**
     * This event is triggered when this application shouldn't have the ability to be in multi
     * window mode, but in fact it is.
     */
    data object ApplicationInMultiWindowModeViolation : ActivityEvent()
}

data class ActivityState(val flowStarted: Boolean = false) : BaseState

sealed class ActivityEffect : BaseEffect {
    sealed class Navigation : ActivityEffect() {
        data class NavigateToHomeScreenWithSessionExpired(val showErrorDialog: Boolean) :
            Navigation()

        data object NavigateToSplashScreenWithApplicationInsecureError : Navigation()
        data object NavigateToSplashScreenWithApplicationInMultiWindowModeViolation : Navigation()
    }
}

class ActivityViewModel : BaseViewModel<ActivityEvent, ActivityState, ActivityEffect>() {

    private val prefKeys: PrefKeys by inject()
    private val logger: AppLoggerController by inject()
    private val loginInteractor: LoginInteractor by inject()
    private val resourceProvider: ResourceProvider by inject()
    private val appLoggerController: AppLoggerController by inject()
    private val websocketController: WebsocketController by inject()
    private val securityController: SecurityController by inject()
    private val routerHost: RouterHost by inject()

    override fun setInitialState(): ActivityState = ActivityState()

    override fun handleEvents(event: ActivityEvent) {
        when (event) {
            ActivityEvent.LifecycleEvent.OnCreate -> {
                prefKeys.autoLogoutTimestamp = null
                init()
            }

            ActivityEvent.LifecycleEvent.OnPause -> {
                if (prefKeys.isUserLoggedIn() && securityController.isAutoLogoutEnabled()) {
                    prefKeys.autoLogoutTimestamp = DateUtil.getUTCInstant()
                }
            }

            ActivityEvent.LifecycleEvent.OnResume -> {
                if (prefKeys.isUserLoggedIn()) {
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

            ActivityEvent.LifecycleEvent.OnDestroy -> {
                onDestroy()
            }

            ActivityEvent.OnUserInteraction -> {
                performAutoLogoutOperations()
            }

            ActivityEvent.ApplicationDrawOverlayWindowsViolation -> {
                onLogoutUser(showErrorDialog = false)
                setEffect {
                    ActivityEffect.Navigation.NavigateToSplashScreenWithApplicationInsecureError
                }
            }

            ActivityEvent.ApplicationInMultiWindowModeViolation -> {
                onLogoutUser(showErrorDialog = false)
                setEffect {
                    ActivityEffect.Navigation.NavigateToSplashScreenWithApplicationInMultiWindowModeViolation
                }
            }
        }
    }

    /**
     * The actions to perform when the viewModel is instantiated.
     */
    private fun init() {
        viewModelScope.launch {
            logger.d { "authData: ${prefKeys.getUserAuthData()}" }
            websocketController.setWebSocketListener(getWebSocketListener())
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
            ActivityEffect.Navigation.NavigateToHomeScreenWithSessionExpired(showErrorDialog = showErrorDialog)
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
                websocketController.sendRequest(WebsocketRequestType.GetTimestamp).collect {
                    val currentTimestamp =
                        when (it) {
                            is WebsocketSendRequestPartialState.Failed -> {
                                logger.d("ActivityViewModel") { "(performAutoLogoutOperations#sendRequest#Failed) errorMessage: ${it.errorMessage}" }
                                DateUtil.getUTCInstant()
                            }

                            is WebsocketSendRequestPartialState.Success -> {
                                try {
                                    logger.d("ActivityViewModel") { "(performAutoLogoutOperations#sendRequest#Success) timestamp: ${it.websocketDomain.data}" }
                                    Instant.parse(it.websocketDomain.data)
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
     * Returns the websocket client responsible for handling connections, terminations, and more.
     */
    private suspend fun getWebSocketListener(): WebSocketListener {
        val listener = CoroutineWebSocketListener()

        viewModelScope.launch(Dispatchers.IO) {
            listener.eventChannel.consumeEach { webSocketEvent ->
                when (webSocketEvent) {
                    is WebSocketEvent.OnOpen -> {
                        appLoggerController.d("ActivityViewModel") { "The websocket connection has been opened." }
                    }

                    is WebSocketEvent.OnMessage -> {
                        appLoggerController.d("ActivityViewModel") { "Received message: ${webSocketEvent.text}" }

                        try {
                            val websocketDomain =
                                Gson().fromJson(webSocketEvent.text, ResponseWebsocket::class.java)
                                    .toWebsocketDomain()
                            websocketController.onMessageReceived(
                                WebsocketOnMessageReceivedPartialState.Success(websocketDomain)
                            )
                            appLoggerController.d("ActivityViewModel") { "(onMessage) websocketDomain: $websocketDomain" }
                        } catch (e: Exception) {
                            appLoggerController.e("ActivityViewModel") { "(onMessage) Error parsing websocket response: $e" }
                            websocketController.onMessageReceived(
                                WebsocketOnMessageReceivedPartialState.Failed(e.message.orEmpty())
                            )
                        }
                    }

                    is WebSocketEvent.OnClosing -> {
                        appLoggerController.d("ActivityViewModel") { "(onClosing): The websocket server has decided to close the websocket connection.\tcode: ${webSocketEvent.code}" }

                        if (prefKeys.isUserLoggedIn()) {
                            onLogoutUser()
                        }
                    }

                    is WebSocketEvent.OnClosed -> {
                        if (prefKeys.isUserLoggedIn()) {
                            appLoggerController.d("ActivityViewModel") { "(onClosed): isUserLoggedIn: true" }
                            onLogoutUser()
                        }
                        appLoggerController.d("ActivityViewModel") { "(onClosed): Both the websocket client and server have decided to end this websocket connection.\tcode: ${webSocketEvent.code}" }
                    }

                    is WebSocketEvent.OnFailure -> {
                        appLoggerController.d("ActivityViewModel") { "(onFailure): The websocket server has decided to close this websocket connection." }
                        onLogoutUser()
                    }
                }
            }
        }
        return listener
    }
}