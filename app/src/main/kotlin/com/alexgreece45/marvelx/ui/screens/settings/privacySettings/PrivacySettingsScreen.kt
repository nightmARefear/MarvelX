package com.alexgreece45.marvelx.ui.screens.settings.privacySettings

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.common_ui.util.ObserveUiEffect
import com.alexgreece45.marvelx.core.common.util.flows.single_observer_flow.SingleObserverFlow
import com.alexgreece45.marvelx.domain.model_api.user.privacySettings.PrivacySettingsDomain
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.WrapSwitch
import com.alexgreece45.marvelx.ui.components.error.ContentErrorScreen
import com.alexgreece45.marvelx.ui.components.model.ErrorButtonLayout
import com.alexgreece45.marvelx.ui_theme.theme.MarvelXTheme
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.VSpacer
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun PrivacySettingsScreen(
    onEventSent: (PrivacySettingsEvent) -> Unit,
    state: PrivacySettingsState,
    effect: SingleObserverFlow<PrivacySettingsEffect>,
    popBackStack: () -> Unit
) {
    LaunchedEffect(Unit) {
        onEventSent(PrivacySettingsEvent.Init)
    }

    ObserveUiEffect(flow = effect, tag = "PrivacySettingsScreen") { uiEffect ->
        when (uiEffect) {
            PrivacySettingsEffect.Navigation.PopBackStack -> popBackStack()
        }
    }

    state.errorView?.let { errorView ->
        ContentErrorScreen(
            errorTitle = errorView.errorTitle,
            errorSubtitle = errorView.errorSubtitle,
            onClose = { onEventSent(PrivacySettingsEvent.OnBackClicked) },
            errorButtonLayout = ErrorButtonLayout.Single(
                primaryButtonLabel = errorView.primaryButtonLabel,
                onPrimaryButtonClicked = { onEventSent(errorView.event) }
            )
        )
        return
    }

    ContentScreen(
        isLoading = state.isLoading,
        onBack = { onEventSent(PrivacySettingsEvent.OnBackClicked) },
        navigableAction = ScreenNavigableAction.Backable(
            testTag = PrivacySettingsTestTags.BACK_BUTTON,
            onBack = {
                onEventSent(
                    PrivacySettingsEvent.OnBackClicked
                )
            }
        ),
        showToolbar = true,
        toolbarTitle = stringResource(R.string.privacy_settings_screen_toolbar_title)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = paddingValues
        ) {
            state.privacySettings?.let { privacySettings ->
                item {
                    Column(modifier = Modifier.fillParentMaxSize()) {
                        // Header.
                        Text(
                            text = stringResource(R.string.privacy_settings_screen_header_label_visible_to_everyone),
                            style = MaterialTheme.typography.h6
                        )

                        // Items.
                        Column(verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)) {
                            PrivacySettingItem(
                                label = stringResource(R.string.privacy_settings_screen_label_friends_list),
                                isChecked = privacySettings.isFriendsListVisible,
                                testTag = PrivacySettingsTestTags.FRIENDS_LIST_SWITCH,
                                onCheckedToggle = {
                                    onEventSent(
                                        PrivacySettingsEvent.OnFriendsListToggleClicked(
                                            it
                                        )
                                    )
                                }
                            )

                            PrivacySettingItem(
                                label = stringResource(R.string.privacy_settings_screen_label_listings_list),
                                isChecked = privacySettings.isListingsListVisible,
                                testTag = PrivacySettingsTestTags.LISTINGS_LIST_SWITCH,
                                onCheckedToggle = {
                                    onEventSent(
                                        PrivacySettingsEvent.OnListingsListToggleClicked(
                                            it
                                        )
                                    )
                                }
                            )

                            PrivacySettingItem(
                                label = stringResource(R.string.privacy_settings_screen_label_favorites_list),
                                isChecked = privacySettings.isFavoritesListVisible,
                                testTag = PrivacySettingsTestTags.FAVORITES_LIST_SWITCH,
                                onCheckedToggle = {
                                    onEventSent(
                                        PrivacySettingsEvent.OnFavoritesListToggleClicked(
                                            it
                                        )
                                    )
                                }
                            )

                            PrivacySettingItem(
                                label = stringResource(R.string.generic_label_gender),
                                isChecked = privacySettings.isGenderVisible,
                                testTag = PrivacySettingsTestTags.GENDER_SWITCH,
                                onCheckedToggle = {
                                    onEventSent(
                                        PrivacySettingsEvent.OnGenderToggleClicked(
                                            it
                                        )
                                    )
                                }
                            )

                            PrivacySettingItem(
                                label = stringResource(R.string.generic_label_email),
                                isChecked = privacySettings.isEmailVisible,
                                testTag = PrivacySettingsTestTags.EMAIL_SWITCH,
                                onCheckedToggle = {
                                    onEventSent(
                                        PrivacySettingsEvent.OnEmailToggleClicked(
                                            it
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Draws a single privacy settings item to the screen. Essentially, it is a row that has a label to
 * the far left, and at the far right a switch. The state of the switch can be altered through
 * [onCheckedToggle]. The label of this item is set through [label] and the switch's current state
 * through [isChecked].
 */
@Composable
private fun PrivacySettingItem(
    label: String,
    isChecked: Boolean,
    testTag: String,
    onCheckedToggle: (Boolean) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.subtitle1
            )

            WrapSwitch(
                checked = isChecked,
                onCheckedToggle = onCheckedToggle,
                modifier = Modifier.testTag(testTag)
            )
        }

        VSpacer.Tiny()
        Divider()
    }
}

@Preview(
    apiLevel = 34,
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun PrivacySettingsScreenPreview() {
    MarvelXTheme {
        PrivacySettingsScreen(
            onEventSent = {},
            state = PrivacySettingsState(
                isLoading = false,
                privacySettings = PrivacySettingsDomain(
                    isFriendsListVisible = true,
                    isListingsListVisible = false,
                    isFavoritesListVisible = true,
                    isGenderVisible = false,
                    isEmailVisible = true
                )
            ),
            effect = SingleObserverFlow(emptyFlow()),
            popBackStack = {}
        )
    }
}