package com.alexgreece45.marvelx.ui.screens.settings

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.BuildConfig
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.core.common.config.ConfigLogic
import com.alexgreece45.marvelx.core.common.config.model.AppBuildType
import com.alexgreece45.marvelx.core.network.managers.SessionManager
import com.alexgreece45.marvelx.core.network.managers.model.LogoutReason
import com.alexgreece45.marvelx.data.repository.NetworkInspectorRepository
import com.alexgreece45.marvelx.model.model.SensitiveNetworkDataMode
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.components.IconData
import com.alexgreece45.marvelx.ui.components.sections.model.SectionHeaderData
import com.alexgreece45.marvelx.ui.screens.settings.model.SettingsContract
import com.alexgreece45.marvelx.util.ResourceProvider
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class SettingsViewModel(
    private val resourceProvider: ResourceProvider,
    private val sessionManager: SessionManager,
    private val configLogic: ConfigLogic,
    private val networkInspectorRepository: NetworkInspectorRepository,
) : BaseViewModel<SettingsContract.Event, SettingsContract.State, SettingsContract.Effect>() {

    /**
     * The network inspector (Chucker) only captures traffic in debug/mock builds, so its settings
     * are only offered there.
     */
    private val isNetworkInspectorAvailable: Boolean
        get() = configLogic.appBuildType != AppBuildType.RELEASE

    override fun setInitialState(): SettingsContract.State = SettingsContract.State()

    override fun handleEvents(event: SettingsContract.Event) {
        when (event) {
            SettingsContract.Event.Init -> init()

            SettingsContract.Event.OnBackClicked -> onBackClicked()

            is SettingsContract.Event.NetworkInspectorDialogEvents.OnSensitiveDataModeSelected ->
                onSensitiveNetworkDataModeSelected(event.mode)

            SettingsContract.Event.NetworkInspectorDialogEvents.OnDismissed -> {
                setState { copy(isNetworkInspectorDialogVisible = false) }
            }
        }
    }

    private fun init() {
        setState {
            copy(
                isLoading = false,
                sectionHeadersData = buildList {
                    add(
                        SectionHeaderData(
                            title = resourceProvider.getString(R.string.screen_settings_section_label_privacy_settings),
                            iconData = IconData(
                                iconResource = R.drawable.ic_privacy,
                                contentDescription = R.string.content_description_privacy
                            ),
                            onClick = {
                                setEffect {
                                    SettingsContract.Effect.Navigation.NavigateToPrivacySettingsScreen
                                }
                            }
                        )
                    )
                    add(
                        SectionHeaderData(
                            title = resourceProvider.getString(R.string.screen_settings_section_label_faq),
                            iconData = IconData(
                                iconResource = R.drawable.ic_faq,
                                contentDescription = R.string.content_description_faq
                            ),
                            onClick = {
                                setEffect {
                                    SettingsContract.Effect.Navigation.NavigateToFaqScreen
                                }
                            }
                        )
                    )
                    if (isNetworkInspectorAvailable) {
                        add(
                            SectionHeaderData(
                                title = resourceProvider.getString(R.string.screen_settings_section_label_network_inspector),
                                iconData = IconData(
                                    iconResource = R.drawable.ic_lock,
                                    contentDescription = R.string.content_description_network_inspector
                                ),
                                isNavigable = false,
                                onClick = {
                                    setState { copy(isNetworkInspectorDialogVisible = true) }
                                }
                            )
                        )
                    }
                    add(
                        SectionHeaderData(
                            title = resourceProvider.getString(R.string.profile_screen_section_label_logout),
                            iconData = IconData(
                                iconResource = R.drawable.ic_logout,
                                contentDescription = R.string.content_description_ic_logout
                            ),
                            isNavigable = false,
                            onClick = {
                                sessionManager.triggerLogout(LogoutReason.USER_TRIGGERED)
                            }
                        )
                    )
                },
                appVersion = BuildConfig.VERSION_NAME
            )
        }

        if (isNetworkInspectorAvailable) {
            viewModelScope.launch {
                val mode = networkInspectorRepository.getSensitiveNetworkDataMode()
                setState { copy(sensitiveNetworkDataMode = mode) }
            }
        }
    }

    private fun onSensitiveNetworkDataModeSelected(mode: SensitiveNetworkDataMode) {
        setState {
            copy(
                sensitiveNetworkDataMode = mode,
                isNetworkInspectorDialogVisible = false
            )
        }
        viewModelScope.launch {
            networkInspectorRepository.setSensitiveNetworkDataMode(mode)
        }
    }

    private fun onBackClicked() {
        setEffect { SettingsContract.Effect.Navigation.GoBack }
    }
}
