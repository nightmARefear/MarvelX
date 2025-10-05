package com.alexgreece45.marvelx.ui.screens.friends

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.components.AlertDialogButton
import com.alexgreece45.marvelx.ui.components.AlertDialogError
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.PullRefreshable
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.WrapOutlinedButton
import com.alexgreece45.marvelx.ui.components.effect.ObserveUiEffect
import com.alexgreece45.marvelx.ui.components.error.ContentErrorScreenHeader
import com.alexgreece45.marvelx.ui.components.model.ErrorButtonType
import com.alexgreece45.marvelx.ui.screens.friends.model.FriendsContract
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import kotlinx.coroutines.flow.Flow

@Composable
fun FriendsScreen(
    onEventSent: (FriendsContract.Event) -> Unit,
    state: FriendsContract.State,
    effect: Flow<FriendsContract.Effect>,
    onNavigationEffect: (FriendsContract.Effect.Navigation) -> Unit
) {

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        onEventSent(FriendsContract.Event.Init)
    }

    ObserveUiEffect(flow = effect) { uiEffect ->
        when (uiEffect) {
            is FriendsContract.Effect.Navigation -> onNavigationEffect(uiEffect)
            is FriendsContract.Effect.ShowToastMessage -> Toast.makeText(
                context,
                uiEffect.message,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    ContentScreen(
        isLoading = false,
        navigableAction = ScreenNavigableAction.Backable(
            onBack = {
                onEventSent(FriendsContract.Event.OnBackClicked)
            }
        ),
        onBack = { onEventSent(FriendsContract.Event.OnSystemBack) },
        showToolbar = true,
        toolbarTitle = stringResource(id = R.string.screen_friends_toolbar_title)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
        ) {
            TabRow(selectedTabIndex = state.selectedTabIndex) {
                state.tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = index == state.selectedTabIndex,
                        onClick = { onEventSent(FriendsContract.Event.OnTabClicked(index)) }
                    ) {
                        Text(text = title)
                    }
                }
            }

            PullRefreshable(
                onRefresh = { onEventSent(FriendsContract.Event.OnPullToRefresh) },
                isRefreshing = state.isTabRefreshing
            ) {
                when (state.selectedTabIndex) {
                    0 -> {

                    }

                    1 -> {

                    }

                    2 -> {

                    }
                }
            }

            state.errorView?.let { error ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp)
                ) {
                    ContentErrorScreenHeader(
                        title = stringResource(R.string.generic_error),
                        subtitle = error.subtitle
                    )

                    WrapOutlinedButton(onClick = error.primaryButtonOnClicked) {
                        Text(
                            text = error.primaryButtonLabel
                        )
                    }
                }
            }

            state.dialogView?.let { dialog ->
                AlertDialogError(
                    isVisible = true,
                    title = dialog.title,
                    errorMessage = dialog.subtitle,
                    confirmButton = AlertDialogButton(
                        label = dialog.positiveButtonLabel,
                        buttonType = ErrorButtonType.Contained,
                        onClick = dialog.positiveButtonOnClicked
                    ),
                    dismissButton = AlertDialogButton(
                        label = dialog.negativeButtonLabel,
                        onClick = dialog.negativeButtonOnClicked
                    )
                )
            }
        }
    }
}
