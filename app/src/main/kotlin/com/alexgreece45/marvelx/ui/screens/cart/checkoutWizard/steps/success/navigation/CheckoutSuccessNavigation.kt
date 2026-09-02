package com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.steps.success.navigation

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import androidx.navigation.toRoute
import com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.steps.success.CheckoutSuccessScreen
import com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.steps.success.CheckoutSuccessViewModel
import com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.steps.success.model.CheckoutSuccessContract
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
internal data class CheckoutSuccessRoute(val orderId: String)

internal fun NavController.navigateToCheckoutSuccessScreen(
    orderId: String,
    navOptionsBuilder: (NavOptionsBuilder.() -> Unit)?
) = navigate(
    route = CheckoutSuccessRoute(orderId),
    navOptions = navOptionsBuilder?.let { navOptions(it) }
)

internal fun NavGraphBuilder.checkoutSuccessScreen(
    goBack: () -> Unit,
    navigateToOrderDetailsScreen: (orderId: String, navOptionsBuilder: NavOptionsBuilder.() -> Unit) -> Unit
) {
    composable<CheckoutSuccessRoute> { navBackStackEntry ->
        val route = navBackStackEntry.toRoute<CheckoutSuccessRoute>()
        val checkoutSuccessViewModel = koinViewModel<CheckoutSuccessViewModel>()

        CheckoutSuccessScreen(
            onEvent = checkoutSuccessViewModel::setEvent,
            state = checkoutSuccessViewModel.uiState.collectAsStateWithLifecycle().value,
            effect = checkoutSuccessViewModel.uiEffect,
            onNavigationEffect = { navigationEffect ->
                when (navigationEffect) {
                    is CheckoutSuccessContract.Effect.Navigation.NavigateToOrderDetailsScreen -> navigateToOrderDetailsScreen(
                        navigationEffect.orderId
                    ) {
                        popUpTo(route) { inclusive = true }
                    }

                    CheckoutSuccessContract.Effect.Navigation.GoBack -> goBack()
                }
            }
        )
    }
}