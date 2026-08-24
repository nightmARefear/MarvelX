package com.alexgreece45.marvelx.ui.screens.orders.requestReturn.navigation

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import androidx.navigation.toRoute
import com.alexgreece45.marvelx.ui.navigation.SavedStateHandleKeys
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.RequestItemReturnScreen
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.RequestItemReturnViewModel
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.RequestItemReturnContract
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.success.navigation.navigateToRequestItemReturnSuccessScreen
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
data class RequestItemReturnRoute(
    val orderId: String,
    val orderItemId: String
)

internal fun NavController.navigateToRequestItemReturnScreen(
    orderId: String,
    orderItemId: String,
    navOptionsBuilder: (NavOptionsBuilder.() -> Unit)? = null
) = navigate(
    route = RequestItemReturnRoute(
        orderId,
        orderItemId
    ),
    navOptions = navOptions {
        navOptionsBuilder?.let { it() }
        launchSingleTop = true
    }
)

internal fun NavGraphBuilder.requestItemReturnScreen(navController: NavController) {
    composable<RequestItemReturnRoute> { navBackStackEntry ->
        val viewModel = koinViewModel<RequestItemReturnViewModel>()
        val route = navBackStackEntry.toRoute<RequestItemReturnRoute>()

        RequestItemReturnScreen(
            onEvent = viewModel::setEvent,
            state = viewModel.uiState.collectAsStateWithLifecycle().value,
            effect = viewModel.uiEffect,
            snackbarEffects = viewModel.snackbarEffects,
            onNavigationEffect = { navigationEffect ->
                when (navigationEffect) {
                    RequestItemReturnContract.Effect.Navigation.GoBack -> navController.navigateUp()
                    is RequestItemReturnContract.Effect.Navigation.ExitWithError -> {
                        navController.previousBackStackEntry?.savedStateHandle[SavedStateHandleKeys.snackbarNavigationPayload] =
                            navigationEffect.snackbarNavigationPayload
                        navController.navigateUp()
                    }

                    is RequestItemReturnContract.Effect.Navigation.GoToSuccessScreen -> {
                        navController.navigateToRequestItemReturnSuccessScreen(
                            orderId = navigationEffect.orderId,
                            orderItemId = navigationEffect.orderItemId,
                            orderItemReturnId = navigationEffect.orderItemReturnId,
                            navOptionsBuilder = { popUpTo(route) { inclusive = true } }
                        )
                    }
                }
            }
        )
    }
}