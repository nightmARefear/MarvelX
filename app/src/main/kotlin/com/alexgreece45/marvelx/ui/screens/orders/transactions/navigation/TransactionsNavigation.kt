package com.alexgreece45.marvelx.ui.screens.orders.transactions.navigation

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.alexgreece45.marvelx.ui.screens.orders.orderItemManagement.navigation.navigateToOrderItemManagementScreen
import com.alexgreece45.marvelx.ui.screens.orders.orderItemTrackingHistory.navigation.navigateToOrderItemTrackingHistoryScreen
import com.alexgreece45.marvelx.ui.screens.orders.transactions.TransactionsScreen
import com.alexgreece45.marvelx.ui.screens.orders.transactions.TransactionsViewModel
import com.alexgreece45.marvelx.ui.screens.orders.transactions.model.TransactionsContract
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
internal data object TransactionsRoute

internal fun NavController.navigateToTransactionsScreen() = navigate(route = TransactionsRoute)

internal fun NavGraphBuilder.transactionsScreen(
    navController: NavController
) {
    composable<TransactionsRoute> {
        val viewModel = koinViewModel<TransactionsViewModel>()

        TransactionsScreen(
            onEvent = viewModel::setEvent,
            state = viewModel.uiState.collectAsStateWithLifecycle().value,
            effect = viewModel.uiEffect,
            snackbarEffects = viewModel.snackbarEffects,
            onNavigationEffect = { navigationEffect ->
                when (navigationEffect) {
                    TransactionsContract.Effect.Navigation.GoBack -> navController.navigateUp()

                    is TransactionsContract.Effect.Navigation.GoToOrderItemTrackingHistoryScreen ->
                        navController.navigateToOrderItemTrackingHistoryScreen(
                            orderId = navigationEffect.orderId,
                            orderItemId = navigationEffect.orderItemId,
                            orderItemTitle = navigationEffect.orderItemTitle,
                            orderItemCoverImageUrl = navigationEffect.orderItemCoverImageUrl,
                            orderItemPriceLabel = navigationEffect.orderItemPriceLabel,
                            sellerUserId = navigationEffect.sellerUserId
                        )

                    is TransactionsContract.Effect.Navigation.GoToOrderItemManagementScreen ->
                        navController.navigateToOrderItemManagementScreen(
                            orderId = navigationEffect.orderId,
                            orderItemId = navigationEffect.orderItemId
                        )
                }
            }
        )
    }
}
