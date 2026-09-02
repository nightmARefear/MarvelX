package com.alexgreece45.marvelx.ui.screens.orders.orderDetails.navigation

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navOptions
import com.alexgreece45.marvelx.ui.helper.getDeepLinkBasePath
import com.alexgreece45.marvelx.ui.screens.orders.orderDetails.OrderDetailsScreen
import com.alexgreece45.marvelx.ui.screens.orders.orderDetails.OrderDetailsViewModel
import com.alexgreece45.marvelx.ui.screens.orders.orderDetails.model.OrderDetailsContract
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
internal data class OrderDetailsRoute(val orderId: String)

fun NavController.navigateToOrderDetailsScreen(
    orderId: String,
    navOptionsBuilder: (NavOptionsBuilder.() -> Unit)? = null
) = navigate(
    route = OrderDetailsRoute(orderId),
    navOptions = navOptionsBuilder?.let { navOptions(it) })

fun NavGraphBuilder.orderDetailsScreen(
    goBack: () -> Unit,
    navigateToOrderItemTrackingHistoryScreen: (
        orderId: String,
        orderItemId: String,
        orderItemTitle: String,
        orderItemCoverImageUrl: String,
        orderItemPriceLabel: String,
        sellerUserId: String
    ) -> Unit
) {
    composable<OrderDetailsRoute>(
        deepLinks = listOf(
            navDeepLink<OrderDetailsRoute>(
                getDeepLinkBasePath(screenName = OrderDetailsRoute::class.simpleName.orEmpty())
            )
        )
    ) {
        val viewModel = koinViewModel<OrderDetailsViewModel>()
        OrderDetailsScreen(
            onEvent = viewModel::setEvent,
            state = viewModel.uiState.collectAsStateWithLifecycle().value,
            effect = viewModel.uiEffect,
            onNavigationEffect = { navigationEffect ->
                when (navigationEffect) {
                    OrderDetailsContract.Effect.Navigation.GoBack -> goBack()
                    is OrderDetailsContract.Effect.Navigation.NavigateToOrderItemTrackingHistoryScreen -> navigateToOrderItemTrackingHistoryScreen(
                        navigationEffect.orderId,
                        navigationEffect.orderItemId,
                        navigationEffect.orderItemTitle,
                        navigationEffect.orderItemCoverImageUrl,
                        navigationEffect.orderItemPriceLabel,
                        navigationEffect.sellerUserId
                    )
                }
            }
        )
    }
}