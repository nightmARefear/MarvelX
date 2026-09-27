package com.alexgreece45.marvelx.ui.screens.settings

import android.content.res.Configuration
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.common_ui.util.ObserveUiEffect
import com.alexgreece45.marvelx.core.common.util.flows.single_observer_flow.SingleObserverFlow
import com.alexgreece45.marvelx.model.model.SensitiveNetworkDataMode
import com.alexgreece45.marvelx.ui.components.AlertDialogButton
import com.alexgreece45.marvelx.ui.components.AlertDialogError
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.sections.SectionListHeader
import com.alexgreece45.marvelx.ui.screens.settings.model.SettingsContract
import com.alexgreece45.marvelx.ui_theme.theme.MarvelXTheme
import com.alexgreece45.marvelx.ui_theme.theme.values.customColors
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.SPACING_TINY
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun SettingsScreen(
    onEvent: (SettingsContract.Event) -> Unit,
    state: SettingsContract.State,
    effect: SingleObserverFlow<SettingsContract.Effect>,
    onNavigationEffect: (SettingsContract.Effect.Navigation) -> Unit
) {
    val context = LocalContext.current

    ObserveUiEffect(flow = effect, tag = "SettingsScreen") { uiEffect ->
        when (uiEffect) {
            is SettingsContract.Effect.Navigation -> onNavigationEffect(uiEffect)
            is SettingsContract.Effect.ShowToast -> {
                Toast.makeText(context, uiEffect.toastMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    LaunchedEffect(Unit) {
        onEvent(SettingsContract.Event.Init)
    }

    if (state.isNetworkInspectorDialogVisible) {
        NetworkInspectorDialog(
            selectedMode = state.sensitiveNetworkDataMode,
            onModeSelected = { mode ->
                onEvent(SettingsContract.Event.NetworkInspectorDialogEvents.OnSensitiveDataModeSelected(mode))
            },
            onDismiss = {
                onEvent(SettingsContract.Event.NetworkInspectorDialogEvents.OnDismissed)
            }
        )
    }

    ContentScreen(
        isLoading = state.isLoading,
        navigableAction = ScreenNavigableAction.Backable(
            testTag = SettingsTestTags.BACK_BUTTON,
            onBack = {
                onEvent(SettingsContract.Event.OnBackClicked)
            }
        ),
        showToolbar = true,
        toolbarTitle = stringResource(R.string.screen_settings_toolbar_title),
        stickyBottom = {
            Text(
                text = stringResource(R.string.screen_settings_label_version, state.appVersion),
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.customColors.genericRed,
                style = MaterialTheme.typography.subtitle2
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
        ) {
            SectionListHeader(sectionHeadersData = state.sectionHeadersData)
        }
    }
}

/**
 * Lets the developer choose how the network inspector (Chucker) treats sensitive data. Selecting
 * an option applies it and closes the dialog.
 */
@Composable
private fun NetworkInspectorDialog(
    selectedMode: SensitiveNetworkDataMode,
    onModeSelected: (SensitiveNetworkDataMode) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialogError(
        title = stringResource(R.string.screen_settings_network_inspector_dialog_title),
        errorMessage = {
            Column(
                modifier = Modifier.selectableGroup(),
                verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)
            ) {
                Text(text = stringResource(R.string.screen_settings_network_inspector_dialog_description))

                SensitiveNetworkDataMode.entries.forEach { mode ->
                    NetworkInspectorModeOption(
                        mode = mode,
                        isSelected = mode == selectedMode,
                        onSelect = { onModeSelected(mode) }
                    )
                }
            }
        },
        confirmButton = AlertDialogButton(
            label = stringResource(R.string.generic_cancel_button),
            onClick = onDismiss
        ),
        onDismissRequest = onDismiss
    )
}

@Composable
private fun NetworkInspectorModeOption(
    mode: SensitiveNetworkDataMode,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = isSelected, onClick = onSelect, role = Role.RadioButton),
        horizontalArrangement = Arrangement.spacedBy(SPACING_TINY.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // The row handles the click, so the radio button itself doesn't.
        RadioButton(selected = isSelected, onClick = null)

        Column {
            Text(
                text = stringResource(mode.labelResource),
                style = MaterialTheme.typography.subtitle1
            )
            Text(
                text = stringResource(mode.descriptionResource),
                style = MaterialTheme.typography.body2
            )
        }
    }
}

@get:StringRes
private val SensitiveNetworkDataMode.labelResource: Int
    get() = when (this) {
        SensitiveNetworkDataMode.MASKED -> R.string.screen_settings_network_inspector_option_masked
        SensitiveNetworkDataMode.UNMASKED -> R.string.screen_settings_network_inspector_option_unmasked
        SensitiveNetworkDataMode.HIDDEN -> R.string.screen_settings_network_inspector_option_hidden
    }

@get:StringRes
private val SensitiveNetworkDataMode.descriptionResource: Int
    get() = when (this) {
        SensitiveNetworkDataMode.MASKED -> R.string.screen_settings_network_inspector_option_masked_description
        SensitiveNetworkDataMode.UNMASKED -> R.string.screen_settings_network_inspector_option_unmasked_description
        SensitiveNetworkDataMode.HIDDEN -> R.string.screen_settings_network_inspector_option_hidden_description
    }

@Preview(
    apiLevel = 34,
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun SettingsScreenNetworkInspectorDialogPreview() {
    MarvelXTheme {
        SettingsScreen(
            onEvent = {},
            state = SettingsContract.State(
                isLoading = false,
                appVersion = "1.0-DEBUG",
                sensitiveNetworkDataMode = SensitiveNetworkDataMode.MASKED,
                isNetworkInspectorDialogVisible = true
            ),
            effect = SingleObserverFlow(emptyFlow()),
            onNavigationEffect = {}
        )
    }
}
