package com.alexgreece45.marvelx.ui.screens.orders.requestReturn.success.navigation

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import androidx.navigation.toRoute
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.navigation.navigateToRequestItemReturnScreen
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.success.RequestItemReturnSuccessScreen
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.success.RequestItemReturnSuccessViewModel
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.success.model.RequestItemReturnSuccessContract
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
data class RequestItemReturnSuccessRoute(
    val orderId: String,
    val orderItemId: String,
    val orderItemReturnId: String
)

internal fun NavController.navigateToRequestItemReturnSuccessScreen(
    orderId: String,
    orderItemId: String,
    orderItemReturnId: String,
    navOptionsBuilder: NavOptionsBuilder.() -> Unit
) = navigate(
    route = RequestItemReturnSuccessRoute(orderId, orderItemId, orderItemReturnId),
    navOptions = navOptions {
        navOptionsBuilder()
        launchSingleTop = true
    }
)

internal fun NavGraphBuilder.requestItemReturnSuccessScreen(navController: NavController) {
    composable<RequestItemReturnSuccessRoute> { navBackStackEntry ->
        val viewModel = koinViewModel<RequestItemReturnSuccessViewModel>()
        val route = navBackStackEntry.toRoute<RequestItemReturnSuccessRoute>()

        RequestItemReturnSuccessScreen(
            onEvent = viewModel::setEvent,
            state = viewModel.uiState.collectAsStateWithLifecycle().value,
            effect = viewModel.uiEffect,
            onNavigationEffect = { navigationEffect ->
                when (navigationEffect) {
                    RequestItemReturnSuccessContract.Effect.Navigation.GoBack -> navController.navigateUp()
                    is RequestItemReturnSuccessContract.Effect.Navigation.NavigateToRequestItemReturnScreen -> {
                        navController.navigateToRequestItemReturnScreen(
                            orderId = navigationEffect.orderId,
                            orderItemId = navigationEffect.orderItemId,
                            navOptionsBuilder = { popUpTo(route) { inclusive = true } }
                        )
                    }
                }
            }
        )
    }
}