package com.alexgreece45.marvelx.ui.screens.orders.orderItemTrackingHistory.navigation

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.alexgreece45.marvelx.ui.navigation.Screen
import com.alexgreece45.marvelx.ui.screens.chat.conversation.navigation.navigateToOrderItemConversationScreen
import com.alexgreece45.marvelx.ui.screens.orders.orderItemTrackingHistory.OrderItemTrackingHistoryScreen
import com.alexgreece45.marvelx.ui.screens.orders.orderItemTrackingHistory.OrderItemTrackingHistoryViewModel
import com.alexgreece45.marvelx.ui.screens.orders.orderItemTrackingHistory.model.OrderItemTrackingHistoryContract
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.navigation.navigateToRequestItemReturnScreen
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
internal data class OrderItemTrackingHistoryRoute(
    val orderId: String,
    val orderItemId: String,
    val orderItemTitle: String,
    val orderItemCoverImageUrl: String,
    val orderItemPriceLabel: String,
    val sellerUserId: String
)

internal fun NavController.navigateToOrderItemTrackingHistoryScreen(
    orderId: String,
    orderItemId: String,
    orderItemTitle: String,
    orderItemCoverImageUrl: String,
    orderItemPriceLabel: String,
    sellerUserId: String
) = navigate(
    route = OrderItemTrackingHistoryRoute(
        orderId,
        orderItemId,
        orderItemTitle,
        orderItemCoverImageUrl,
        orderItemPriceLabel,
        sellerUserId
    )
)

internal fun NavGraphBuilder.orderItemTrackingHistoryScreen(
    navController: NavController
) {
    composable<OrderItemTrackingHistoryRoute> {
        val viewModel = koinViewModel<OrderItemTrackingHistoryViewModel>()

        OrderItemTrackingHistoryScreen(
            onEvent = viewModel::setEvent,
            state = viewModel.uiState.collectAsStateWithLifecycle().value,
            effect = viewModel.uiEffect,
            onNavigationEffect = { navigationEffect ->
                when (navigationEffect) {
                    OrderItemTrackingHistoryContract.Effect.Navigation.GoBack -> navController.navigateUp()
                    is OrderItemTrackingHistoryContract.Effect.Navigation.GoToProfileDetailsScreen -> navController.navigate(
                        Screen.ProfileDetailsScreen.createNavRoute(navigationEffect.userId)
                    )

                    is OrderItemTrackingHistoryContract.Effect.Navigation.GoToRequestItemReturnScreen -> navController.navigateToRequestItemReturnScreen(
                        orderId = navigationEffect.orderId,
                        orderItemId = navigationEffect.orderItemId
                    )

                    is OrderItemTrackingHistoryContract.Effect.Navigation.GoToConversationScreen ->
                        navController.navigateToOrderItemConversationScreen(
                            orderId = navigationEffect.orderId,
                            orderItemId = navigationEffect.orderItemId
                        )
                }
            }
        )
    }
}