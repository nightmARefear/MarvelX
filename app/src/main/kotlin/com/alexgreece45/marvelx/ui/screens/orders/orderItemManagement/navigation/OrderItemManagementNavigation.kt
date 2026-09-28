package com.alexgreece45.marvelx.ui.screens.orders.orderItemManagement.navigation

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.alexgreece45.marvelx.ui.helper.getDeepLinkBasePath
import com.alexgreece45.marvelx.ui.screens.chat.conversation.navigation.navigateToOrderItemConversationScreen
import com.alexgreece45.marvelx.ui.screens.orders.orderItemManagement.OrderItemManagementScreen
import com.alexgreece45.marvelx.ui.screens.orders.orderItemManagement.OrderItemManagementViewModel
import com.alexgreece45.marvelx.ui.screens.orders.orderItemManagement.model.OrderItemManagementContract
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.navigation.navigateToRequestItemReturnScreen
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
internal data class OrderItemManagementRoute(
    val orderId: String,
    val orderItemId: String
)

internal fun NavController.navigateToOrderItemManagementScreen(
    orderId: String,
    orderItemId: String
) = navigate(
    route = OrderItemManagementRoute(
        orderId,
        orderItemId
    )
)

internal fun NavGraphBuilder.orderItemManagementScreen(
    navController: NavController
) {
    composable<OrderItemManagementRoute>(
        // Opened by the seller's `ListingPurchase` push and inbox entry.
        deepLinks = listOf(
            navDeepLink<OrderItemManagementRoute>(
                getDeepLinkBasePath(screenName = OrderItemManagementRoute::class.simpleName.orEmpty())
            )
        )
    ) {
        val viewModel = koinViewModel<OrderItemManagementViewModel>()

        OrderItemManagementScreen(
            onEvent = viewModel::setEvent,
            state = viewModel.uiState.collectAsStateWithLifecycle().value,
            effect = viewModel.uiEffect,
            onNavigationEffect = { navigationEffect ->
                when (navigationEffect) {
                    OrderItemManagementContract.Effect.Navigation.GoBack -> navController.navigateUp()
                    is OrderItemManagementContract.Effect.Navigation.GoToRequestItemReturnScreen -> {
                        navController.navigateToRequestItemReturnScreen(
                            orderId = navigationEffect.orderId,
                            orderItemId = navigationEffect.orderItemId
                        )
                    }

                    is OrderItemManagementContract.Effect.Navigation.GoToConversation -> {
                        navController.navigateToOrderItemConversationScreen(
                            orderId = navigationEffect.orderId,
                            orderItemId = navigationEffect.orderItemId
                        )
                    }
                }
            }
        )
    }
}