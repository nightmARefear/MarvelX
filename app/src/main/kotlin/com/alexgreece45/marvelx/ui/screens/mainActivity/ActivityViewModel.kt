package com.alexgreece45.marvelx.ui.screens.mainActivity

import android.net.Uri
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.alexgreece45.marvelx.controllers.AppLogger
import com.alexgreece45.marvelx.controllers.DeepLinkController
import com.alexgreece45.marvelx.controllers.HandleDeepLinkActionResult
import com.alexgreece45.marvelx.controllers.PrefKeys
import com.alexgreece45.marvelx.controllers.SecurityController
import com.alexgreece45.marvelx.controllers.WebsocketController
import com.alexgreece45.marvelx.controllers.model.security.SecurityViolation
import com.alexgreece45.marvelx.controllers.model.websocket.WebsocketOnMessageReceivedPartialState
import com.alexgreece45.marvelx.controllers.model.websocket.WebsocketRequestType
import com.alexgreece45.marvelx.controllers.model.websocket.WebsocketSendRequestPartialState
import com.alexgreece45.marvelx.domain.interactors.LoginInteractor
import com.alexgreece45.marvelx.domain.interactors.LogoutPartialState
import com.alexgreece45.marvelx.domain.model_websocket.toWebsocketDomain
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.network_logic.models_websocket.ResponseWebsocket
import com.alexgreece45.marvelx.ui.helper.model.DeepLinkAction
import com.alexgreece45.marvelx.ui.navigation.router.RouterHost
import com.alexgreece45.marvelx.ui.screens.mainActivity.model.CoroutineWebSocketListener
import com.alexgreece45.marvelx.ui.screens.mainActivity.model.WebSocketEvent
import com.alexgreece45.marvelx.util.DateUtil
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.constants.ConstantsSecurity
import com.alexgreece45.marvelx.util.constants.ConstantsWorker
import com.alexgreece45.marvelx.util.extensions.differenceInMinutes
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

        /**
         * This event is executed when the activity is destroyed. [willBeRecreated] defines whether
         * the activity will be recreated indicating that the onDestroy function was called due to a
         * configuration change, and not because the user/OS closed/killed the app.
         *
         * If [willBeRecreated] is `true`, then the user isn't logged out.
         */
        data class OnDestroy(val willBeRecreated: Boolean) : LifecycleEvent()
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

    data class OnDeepLinkReceived(val uri: Uri) : ActivityEvent()
}

data class ActivityState(val flowStarted: Boolean = false) : BaseState

sealed class ActivityEffect : BaseEffect {
    data class OpenOrCacheDeepLink(val deepLinkAction: DeepLinkAction) :
        ActivityEffect()

    sealed class Navigation : ActivityEffect() {
        data class NavigateToHomeScreenWithSessionExpired(val showErrorDialog: Boolean) :
            Navigation()

        data class NavigateToSplashScreenWithSecurityViolation(val securityViolation: SecurityViolation) :
            Navigation()
    }
}

class ActivityViewModel : BaseViewModel<ActivityEvent, ActivityState, ActivityEffect>() {

    private val prefKeys: PrefKeys by inject()
    private val logger: AppLogger by inject()
    private val loginInteractor: LoginInteractor by inject()
    private val resourceProvider: ResourceProvider by inject()
    private val websocketController: WebsocketController by inject()
    private val securityController: SecurityController by inject()
    private val routerHost: RouterHost by inject()
    private val deepLinkController: DeepLinkController by inject()

    override fun setInitialState(): ActivityState = ActivityState()

    override fun handleEvents(event: ActivityEvent) {
        when (event) {
            ActivityEvent.LifecycleEvent.OnCreate -> {
                prefKeys.autoLogoutTimestamp = null
                setWebsocketListener()
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

            is ActivityEvent.LifecycleEvent.OnDestroy -> {
                if (!event.willBeRecreated) {
                    onDestroy()
                }
            }

            ActivityEvent.OnUserInteraction -> {
                performAutoLogoutOperations()
            }

            ActivityEvent.ApplicationDrawOverlayWindowsViolation -> {
                onLogoutUser(showErrorDialog = false)
                setEffect {
                    ActivityEffect.Navigation.NavigateToSplashScreenWithSecurityViolation(
                        SecurityViolation.ApplicationDrawOverlayWindows
                    )
                }
            }

            ActivityEvent.ApplicationInMultiWindowModeViolation -> {
                onLogoutUser(showErrorDialog = false)
                setEffect {
                    ActivityEffect.Navigation.NavigateToSplashScreenWithSecurityViolation(
                        SecurityViolation.ApplicationInMultiWindowMode
                    )
                }
            }

            is ActivityEvent.OnDeepLinkReceived -> {
                viewModelScope.launch {
                    deepLinkController.handleDeepLinkUri(event.uri).collect {
                        when (it) {
                            is HandleDeepLinkActionResult.Success -> {
                                setEffect {
                                    ActivityEffect.OpenOrCacheDeepLink(it.deepLinkAction)
                                }
                            }

                            HandleDeepLinkActionResult.Failed -> {}
                        }
                    }
                }
            }
        }
    }

    /**
     * The actions to perform when the viewModel is instantiated.
     */
    private fun setWebsocketListener() {
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
     * Returns the websocket listener responsible for handling connections, terminations, and more.
     */
    private suspend fun getWebSocketListener(): WebSocketListener {
        val listener = CoroutineWebSocketListener()

        viewModelScope.launch(Dispatchers.IO) {
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

                        if (prefKeys.isUserLoggedIn()) {
                            onLogoutUser()
                        }
                    }

                    is WebSocketEvent.OnClosed -> {
                        if (prefKeys.isUserLoggedIn()) {
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
