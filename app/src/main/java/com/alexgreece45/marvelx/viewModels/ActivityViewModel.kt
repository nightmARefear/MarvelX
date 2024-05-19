package com.alexgreece45.marvelx.viewModels

import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.controllers.AppLoggerController
import com.alexgreece45.marvelx.controllers.PrefKeys
import com.alexgreece45.marvelx.controllers.SecurityController
import com.alexgreece45.marvelx.controllers.WebsocketController
import com.alexgreece45.marvelx.interactors.LoginInteractor
import com.alexgreece45.marvelx.interactors.LogoutPartialState
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
import com.alexgreece45.marvelx.util.extensions.differenceInSeconds
import kotlinx.coroutines.launch
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.koin.core.component.inject

data class MainActivityErrorDialog(
    val title: String,
    val errorMessage: String,
    val onOkClick: () -> Unit
)

sealed class ActivityEvent : BaseEvent {
    sealed class LifecycleEvent : ActivityEvent() {
        data object OnCreate : LifecycleEvent()
        data object OnPause : LifecycleEvent()
        data object OnResume : LifecycleEvent()
        data object OnDestroy : LifecycleEvent()

    }

    /**
     * This event is triggered when this application shouldn't have the ability to draw itself as an
     * overlay window over other applications, but in fact it does.
     */
    data object ApplicationDrawOverlayWindowsViolation : ActivityEvent()

    /**
     * This event is triggered every time the user taps on the screen.
     */
    data object OnTapGestureDetected : ActivityEvent()
}

data class ActivityState(
    /**
     * Controls whether the error dialog should appear.
     */
    val mainActivityErrorDialog: MainActivityErrorDialog? = null
) : BaseState

sealed class ActivityEffect : BaseEffect {
    sealed class Navigation : ActivityEffect() {
        data class NavigateToHomeScreenWithSessionExpired(val showErrorDialog: Boolean) : Navigation()
        data object NavigateToSplashScreenWithApplicationInsecureError : Navigation()
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
                        false -> onLogoutUser()

                        true -> {
                            if (routerHost.isCurrentRouteIncludedInAutoLogout()) {
                                performAutoLogoutOperations()
                            }
                        }
                    }
                }
            }

            ActivityEvent.LifecycleEvent.OnDestroy -> {
                onDestroy()
            }

            ActivityEvent.ApplicationDrawOverlayWindowsViolation -> {
                onLogoutUser(showErrorDialog = false)
                setEffect {
                    ActivityEffect.Navigation.NavigateToSplashScreenWithApplicationInsecureError
                }
            }

            ActivityEvent.OnTapGestureDetected -> {
                if (routerHost.isCurrentRouteIncludedInAutoLogout()) {
                    if (prefKeys.isUserLoggedIn() && prefKeys.shouldRememberUserSession()) {
                        performAutoLogoutOperations()
                    }
                }
            }
        }
    }

    /**
     * The actions to perform when the viewModel is instantiated.
     */
    private fun init() {
        logger.d { "authData: ${prefKeys.getUserAuthData()}" }
        websocketController.setWebSocketListener(getWebSocketListener())
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
     * The actions to perform to auto-logout the user if the timeout period has been exceeded.
     */
    private fun performAutoLogoutOperations() {
        if (securityController.isAutoLogoutEnabled()) {
            prefKeys.autoLogoutTimestamp?.let {
                if (DateUtil.getUTCInstant()
                        .differenceInMinutes(it) >= ConstantsSecurity.AutoLogout.TIMEOUT_PERIOD
                ) {
                    onLogoutUser()
                }
            } ?: {
                // Since the auto logout timestamp was null, set it to the current timestamp.
                prefKeys.autoLogoutTimestamp = DateUtil.getUTCInstant()
            }
        }
    }

    /**
     * The websocket client responsible for handling connections, terminations, and more.
     */
    private fun getWebSocketListener(): WebSocketListener {
        return object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                appLoggerController.d("ActivityViewModel") { "The websocket connection has been opened." }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                appLoggerController.d("ActivityViewModel") { "Received message: $text" }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosing(webSocket, code, reason)
                appLoggerController.d("ActivityViewModel") { "(onClosing): The websocket server has decided to close the websocket connection.\tcode: $code" }

                if (prefKeys.isUserLoggedIn()) {
                    onLogoutUser()
                }
            }

            // Only executed when websocketController#close() is called.
            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)

                if (prefKeys.isUserLoggedIn()) {
                    appLoggerController.d("ActivityViewModel") { "(onClosed): isUserLoggedIn: true" }
                    onLogoutUser()
                }
                appLoggerController.d("ActivityViewModel") { "(onClosed): Both the websocket client and server have decided to end this websocket connection.\tcode: $code" }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                appLoggerController.d("ActivityViewModel") { "(onFailure): The websocket server has decided to close this websocket connection." }
                onLogoutUser()
            }
        }
    }
}