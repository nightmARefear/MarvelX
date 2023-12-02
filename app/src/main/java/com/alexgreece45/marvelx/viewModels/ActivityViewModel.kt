package com.alexgreece45.marvelx.viewModels

import com.alexgreece45.marvelx.controllers.AppLoggerController
import com.alexgreece45.marvelx.controllers.AuthController
import com.alexgreece45.marvelx.controllers.PrefKeys
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.mvi.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

sealed class ActivityEvent : BaseEvent {
    data object InitView : ActivityEvent()

    data class SetUserId(val userId: Int) : ActivityEvent()
}

data class ActivityState(
    val userId: Int = -1
) : BaseState {
    companion object {
        fun getDefaultValues(): ActivityState =
            ActivityState(
                userId = -1
            )
    }
}

sealed class ActivityEffect : BaseEffect

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val authController: AuthController,
    private val prefKeys: PrefKeys,
    private val logger: AppLoggerController
) : BaseViewModel<ActivityEvent, ActivityState, ActivityEffect>() {
    override fun setInitialState(): ActivityState {
        return ActivityState.getDefaultValues()
    }

    override fun handleEvents(event: ActivityEvent) {
        when (event) {
            ActivityEvent.InitView -> {
                initView()
            }

            is ActivityEvent.SetUserId -> {
                setState {
                    copy(
                        userId = event.userId
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