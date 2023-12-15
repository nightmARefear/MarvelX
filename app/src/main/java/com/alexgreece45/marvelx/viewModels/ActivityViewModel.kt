package com.alexgreece45.marvelx.viewModels

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.controllers.AppLoggerController
import com.alexgreece45.marvelx.controllers.AuthController
import com.alexgreece45.marvelx.controllers.PrefKeys
import com.alexgreece45.marvelx.interactors.LogoutPartialState
import com.alexgreece45.marvelx.interactors.UserInteractor
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.util.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MainActivityErrorDialog(
    val title: String,
    val errorMessage: String,
    val onOkClick: ActivityEvent
)

sealed class ActivityEvent : BaseEvent {
    data object InitView : ActivityEvent()
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

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val authController: AuthController,
    private val prefKeys: PrefKeys,
    private val logger: AppLoggerController,
    private val userInteractor: UserInteractor,
    private val resourceProvider: ResourceProvider
) : BaseViewModel<ActivityEvent, ActivityState, ActivityEffect>() {
    override fun setInitialState(): ActivityState = ActivityState()

    override fun handleEvents(event: ActivityEvent) {
        when (event) {
            ActivityEvent.InitView -> {
                initView()
            }

            is ActivityEvent.SetUserId -> {
//                setState {
//                    copy(
//                        userId = event.userId
//                    )
//                }
            }

            is ActivityEvent.LogoutUser -> {
                setEffect {
                    ActivityEffect.Navigation.NavigateToHomeScreen
                }
                viewModelScope.launch {
                    userInteractor.logout().collect {
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

            ActivityEvent.ErrorDialog.OnOkClick -> {
                setState {
                    copy(
                        mainActivityErrorDialog = null
                    )
                }
            }
        }
    }

    private fun initView() {
        authController.getAccessToken()?.let { accessToken ->
            logger.d { "accessToken : $accessToken" }
        }
        authController.getRefreshToken()?.let { refreshToken ->
            logger.d { "refreshToken : $refreshToken" }
        }
        logger.d { "authData: ${prefKeys.getUserAuthData()}" }
    }
}