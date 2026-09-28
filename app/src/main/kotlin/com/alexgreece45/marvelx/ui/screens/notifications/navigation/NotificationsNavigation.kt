package com.alexgreece45.marvelx.ui.screens.notifications.navigation

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.alexgreece45.marvelx.ui.helper.openDeepLinkActionKeepingBackStack
import com.alexgreece45.marvelx.ui.screens.notifications.NotificationsScreen
import com.alexgreece45.marvelx.ui.screens.notifications.NotificationsViewModel
import com.alexgreece45.marvelx.ui.screens.notifications.model.NotificationsContract
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
internal data object NotificationsRoute

internal fun NavController.navigateToNotificationsScreen() = navigate(route = NotificationsRoute)

internal fun NavGraphBuilder.notificationsScreen(
    navController: NavController
) {
    composable<NotificationsRoute> {
        val viewModel = koinViewModel<NotificationsViewModel>()

        NotificationsScreen(
            onEvent = viewModel::setEvent,
            state = viewModel.uiState.collectAsStateWithLifecycle().value,
            effect = viewModel.uiEffect,
            snackbarEffects = viewModel.snackbarEffects,
            onNavigationEffect = { navigationEffect ->
                when (navigationEffect) {
                    NotificationsContract.Effect.Navigation.GoBack -> navController.navigateUp()

                    // Same destination as tapping the push, but back returns to the inbox.
                    is NotificationsContract.Effect.Navigation.OpenDeepLinkAction ->
                        navController.openDeepLinkActionKeepingBackStack(navigationEffect.deepLinkAction)
                }
            }
        )
    }
}
