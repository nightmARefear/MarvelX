package com.alexgreece45.marvelx.viewModels

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.controllers.AppLoggerController
import com.alexgreece45.marvelx.controllers.PrefKeys
import com.alexgreece45.marvelx.controllers.WebsocketController
import com.alexgreece45.marvelx.interactors.LoginInteractor
import com.alexgreece45.marvelx.interactors.LogoutPartialState
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.util.ResourceProvider
import kotlinx.coroutines.launch
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.koin.core.component.inject

data class MainActivityErrorDialog(
    val title: String,
    val errorMessage: String,
    val onOkClick: ActivityEvent
)

sealed class ActivityEvent : BaseEvent {
    data object Init : ActivityEvent()
    data object LogoutUser : ActivityEvent()

    sealed class ErrorDialog : ActivityEvent() {
        data object OnOkClick : ErrorDialog()
    }

    data class SetUserId(val userId: Int) : ActivityEvent()
}

data class ActivityState(
    val userId: Int = -1,

    /**
     * Controls whether the error dialog should appear.
     */
    val mainActivityErrorDialog: MainActivityErrorDialog? = null
) : BaseState

sealed class ActivityEffect : BaseEffect {
    sealed class Navigation : ActivityEffect() {
        data object NavigateToHomeScreen : Navigation()
    }
}

class ActivityViewModel : BaseViewModel<ActivityEvent, ActivityState, ActivityEffect>() {

    private val prefKeys: PrefKeys by inject()
    private val logger: AppLoggerController by inject()
    private val loginInteractor: LoginInteractor by inject()
    private val resourceProvider: ResourceProvider by inject()
    private val appLoggerController: AppLoggerController by inject()
    private val websocketController: WebsocketController by inject()

    override fun setInitialState(): ActivityState = ActivityState()

    override fun handleEvents(event: ActivityEvent) {
        when (event) {
            ActivityEvent.Init -> {
                init()
            }

            is ActivityEvent.SetUserId -> {
//                setState {
//                    copy(
//                        userId = event.userId
//                    )
//                }
            }

            is ActivityEvent.LogoutUser -> {
                onLogoutUser()
            }

            ActivityEvent.ErrorDialog.OnOkClick -> {
                setState {
                    copy(
                        mainActivityErrorDialog = null
                    )
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
     * The actions to perform when [ActivityEvent.LogoutUser] is called.
     */
    private fun onLogoutUser() {
        setEffect {
            ActivityEffect.Navigation.NavigateToHomeScreen
        }
        viewModelScope.launch {
            loginInteractor.logout().collect {
                when (it) {
                    is LogoutPartialState.Failed, LogoutPartialState.Success -> {
                        setState {
                            copy(
                                mainActivityErrorDialog = MainActivityErrorDialog(
                                    title = resourceProvider.getString(R.string.session_status_session_has_expired_alert_dialog_title),
                                    errorMessage = resourceProvider.getString(R.string.session_status_session_has_expired_alert_dialog_error_message),
                                    onOkClick = ActivityEvent.ErrorDialog.OnOkClick
                                )
                            )
                        }
                    }
                }
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
                onLogoutUser()
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