package com.alexgreece45.marvelx.ui.screens.dashboard.model

import com.alexgreece45.marvelx.data.model.deepLink.DeepLinkAction
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState

sealed interface DashboardContract {

    sealed interface Event : BaseEvent {
        data class Init(val deepLinkAction: DeepLinkAction?) : Event
    }

    data class State(val isLoading: Boolean = false) : BaseState

    sealed interface Effect : BaseEffect {
        data class OpenDeepLinkAction(val deepLinkAction: DeepLinkAction) : Effect
    }
}