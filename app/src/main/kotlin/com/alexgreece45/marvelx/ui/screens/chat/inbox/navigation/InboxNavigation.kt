package com.alexgreece45.marvelx.ui.screens.chat.inbox.navigation

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.alexgreece45.marvelx.ui.navigation.getBottomNavigationBarData
import com.alexgreece45.marvelx.ui.screens.chat.inbox.InboxScreen
import com.alexgreece45.marvelx.ui.screens.chat.inbox.InboxViewModel
import com.alexgreece45.marvelx.ui.screens.chat.inbox.model.InboxContract
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
data object InboxRoute

fun NavController.navigateToInboxScreen() = navigate(InboxRoute)

internal fun NavGraphBuilder.inboxScreen(
    navController: NavController,
    navigateToConversationScreen: (conversationId: String) -> Unit
) {
    composable<InboxRoute> {
        val viewModel = koinViewModel<InboxViewModel>()

        InboxScreen(
            onEvent = viewModel::setEvent,
            state = viewModel.uiState.collectAsStateWithLifecycle().value,
            effect = viewModel.uiEffect,
            onNavigationEffect = { navigationEffect ->
                when (navigationEffect) {
                    is InboxContract.Effect.Navigation.GoToConversation -> {
                        navigateToConversationScreen(navigationEffect.conversationId)
                    }
                }
            },
            bottomNavigationBarData = { getBottomNavigationBarData(navController = navController) }
        )
    }
}
