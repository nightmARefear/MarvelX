package com.alexgreece45.marvelx.ui.screens.settings.model

import com.alexgreece45.marvelx.model.model.SensitiveNetworkDataMode
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.ui.components.sections.model.SectionHeaderData

sealed interface SettingsContract {

    sealed interface Event : BaseEvent {
        data object Init : Event
        data object OnBackClicked : Event

        /**
         * Events of the network inspector (Chucker) dialog, available in debug/mock builds.
         */
        sealed interface NetworkInspectorDialogEvents : Event {
            data class OnSensitiveDataModeSelected(val mode: SensitiveNetworkDataMode) :
                NetworkInspectorDialogEvents

            data object OnDismissed : NetworkInspectorDialogEvents
        }
    }

    data class State(
        val isLoading: Boolean = true,
        val sectionHeadersData: List<SectionHeaderData> = emptyList(),
        val appVersion: String = "",

        /**
         * How the network inspector treats sensitive data. Only relevant when
         * [isNetworkInspectorDialogVisible] can be `true`, i.e., in debug/mock builds.
         */
        val sensitiveNetworkDataMode: SensitiveNetworkDataMode = SensitiveNetworkDataMode.default,
        val isNetworkInspectorDialogVisible: Boolean = false
    ) : BaseState

    sealed interface Effect : BaseEffect {
        data class ShowToast(val toastMessage: String) : Effect

        sealed interface Navigation : Effect {
            data object GoBack : Navigation
            data object NavigateToPrivacySettingsScreen : Navigation
            data object NavigateToFaqScreen : Navigation
            data object NavigateToHomeScreen : Navigation
        }
    }
}
