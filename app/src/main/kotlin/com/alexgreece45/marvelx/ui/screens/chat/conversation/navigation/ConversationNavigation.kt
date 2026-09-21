package com.alexgreece45.marvelx.ui.screens.chat.conversation.navigation

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.alexgreece45.marvelx.ui.navigation.Screen
import com.alexgreece45.marvelx.ui.screens.chat.conversation.ConversationScreen
import com.alexgreece45.marvelx.ui.screens.chat.conversation.ConversationViewModel
import com.alexgreece45.marvelx.ui.screens.chat.conversation.model.ConversationContract
import com.alexgreece45.marvelx.ui.screens.orders.orderItemManagement.navigation.navigateToOrderItemManagementScreen
import com.alexgreece45.marvelx.ui.screens.orders.orderItemTrackingHistory.navigation.navigateToOrderItemTrackingHistoryScreen
import com.alexgreece45.marvelx.util.extensions.composable
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

/**
 * Exactly one of the three targets is set: an existing conversation by id, a direct chat with a
 * user, or the chat about an order item. Property names match [Screen.ChatConversationScreen]'s
 * argument keys so the same ViewModel serves the legacy deep-link route.
 */
@Serializable
internal data class ConversationRoute(
    val conversationId: String? = null,
    val userId: String? = null,
    val orderId: String? = null,
    val orderItemId: String? = null
)

internal fun NavController.navigateToConversationScreen(conversationId: String) =
    navigate(route = ConversationRoute(conversationId = conversationId))

internal fun NavController.navigateToDirectConversationScreen(userId: String) =
    navigate(route = ConversationRoute(userId = userId))

internal fun NavController.navigateToOrderItemConversationScreen(orderId: String, orderItemId: String) =
    navigate(route = ConversationRoute(orderId = orderId, orderItemId = orderItemId))

internal fun NavGraphBuilder.conversationScreen(navController: NavController) {
    composable<ConversationRoute> {
        ConversationDestination(navController = navController)
    }

    // Push notifications and web links resolve through the string-route Screen class.
    composable(screen = Screen.ChatConversationScreen) {
        ConversationDestination(navController = navController)
    }
}

@androidx.compose.runtime.Composable
private fun ConversationDestination(navController: NavController) {
    val viewModel = koinViewModel<ConversationViewModel>()

    ConversationScreen(
        onEvent = viewModel::setEvent,
        state = viewModel.uiState.collectAsStateWithLifecycle().value,
        effect = viewModel.uiEffect,
        onNavigationEffect = { navigationEffect ->
            when (navigationEffect) {
                ConversationContract.Effect.Navigation.GoBack -> navController.navigateUp()

                is ConversationContract.Effect.Navigation.GoToProfileDetails -> navController.navigate(
                    Screen.ProfileDetailsScreen.createNavRoute(navigationEffect.userId)
                )

                is ConversationContract.Effect.Navigation.GoToOrderItemTrackingHistory ->
                    navController.navigateToOrderItemTrackingHistoryScreen(
                        orderId = navigationEffect.orderId,
                        orderItemId = navigationEffect.orderItemId,
                        orderItemTitle = navigationEffect.orderItemTitle,
                        orderItemCoverImageUrl = navigationEffect.orderItemCoverImageUrl,
                        orderItemPriceLabel = navigationEffect.orderItemPriceLabel,
                        sellerUserId = navigationEffect.sellerUserId
                    )

                is ConversationContract.Effect.Navigation.GoToOrderItemManagement ->
                    navController.navigateToOrderItemManagementScreen(
                        orderId = navigationEffect.orderId,
                        orderItemId = navigationEffect.orderItemId
                    )
            }
        }
    )
}
