package com.alexgreece45.marvelx.ui.screens.settings

import com.alexgreece45.marvelx.BuildConfig
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.core.network.managers.SessionManager
import com.alexgreece45.marvelx.core.network.managers.model.LogoutReason
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.components.IconData
import com.alexgreece45.marvelx.ui.components.sections.model.SectionHeaderData
import com.alexgreece45.marvelx.ui.screens.settings.model.SettingsContract
import com.alexgreece45.marvelx.util.ResourceProvider
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class SettingsViewModel(
    private val resourceProvider: ResourceProvider,
    private val sessionManager: SessionManager,
) : BaseViewModel<SettingsContract.Event, SettingsContract.State, SettingsContract.Effect>() {

    override fun setInitialState(): SettingsContract.State = SettingsContract.State()

    override fun handleEvents(event: SettingsContract.Event) {
        when (event) {
            SettingsContract.Event.Init -> init()

            SettingsContract.Event.OnBackClicked -> onBackClicked()
        }
    }

    private fun init() {
        setState {
            copy(
                isLoading = false,
                sectionHeadersData = listOf(
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
                    ),
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
                    ),
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
                    ),
                ),
                appVersion = BuildConfig.VERSION_NAME
            )
        }
    }

    private fun onBackClicked() {
        setEffect { SettingsContract.Effect.Navigation.GoBack }
    }
}
