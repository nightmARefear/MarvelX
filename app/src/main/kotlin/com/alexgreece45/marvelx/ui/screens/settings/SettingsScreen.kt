package com.alexgreece45.marvelx.ui.screens.settings

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.effect.ObserveUiEffect
import com.alexgreece45.marvelx.ui.components.sections.SectionListHeader
import com.alexgreece45.marvelx.ui.screens.settings.model.SettingsContract
import com.alexgreece45.marvelx.ui.theme.values.customColors
import kotlinx.coroutines.flow.Flow

@Composable
fun SettingsScreen(
    onEvent: (SettingsContract.Event) -> Unit,
    state: SettingsContract.State,
    effect: Flow<SettingsContract.Effect>,
    onNavigationEffect: (SettingsContract.Effect.Navigation) -> Unit
) {
    val context = LocalContext.current

    ObserveUiEffect(flow = effect) { uiEffect ->
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

    ContentScreen(
        isLoading = state.isLoading,
        navigableAction = ScreenNavigableAction.Backable(
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
