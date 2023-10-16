package com.alexgreece45.marvelx.domain.viewmodels

import com.alexgreece45.marvelx.domain.controllers.PreferencesController
import com.alexgreece45.marvelx.domain.controllers.PreferencesKeys
import com.alexgreece45.marvelx.ui.base.BaseEffect
import com.alexgreece45.marvelx.ui.base.BaseEvent
import com.alexgreece45.marvelx.ui.base.BaseState
import com.alexgreece45.marvelx.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import logcat.logcat
import javax.inject.Inject

sealed class ActivityEvent : BaseEvent {
    object InitView : ActivityEvent()

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

sealed class ActivityEffect : BaseEffect {

}

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val preferencesKeys: PreferencesKeys,
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
        preferencesKeys.containsUserId().let { keyExists ->
            if (keyExists) {
                logcat { "user_id : ${preferencesKeys.getUserId()}" }
            }
        }
        preferencesKeys.containsUserToken().let { keyExists ->
            if (keyExists) {
                logcat { "user_token : ${preferencesKeys.getUserToken()}" }
            }
        }
    }
}