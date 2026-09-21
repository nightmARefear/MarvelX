package com.alexgreece45.marvelx.ui.screens.cart.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.alexgreece45.marvelx.ui.components.BottomNavigationBarData
import com.alexgreece45.marvelx.ui.navigation.Screen
import com.alexgreece45.marvelx.ui.screens.cart.CartScreen
import com.alexgreece45.marvelx.ui.screens.cart.CartViewModel
import com.alexgreece45.marvelx.ui.screens.cart.model.CartContract
import com.alexgreece45.marvelx.ui.screens.dashboard.navigation.navigateToDashboardScreen
import com.alexgreece45.marvelx.ui.screens.marketplace.listingDetailsScreen.navigation.navigateToListingDetailsScreen
import com.alexgreece45.marvelx.ui.screens.marketplace.navigation.navigateToMarketplaceScreen
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
data object CartRoute

fun NavController.navigateToCartScreen() = navigate(route = CartRoute)

internal fun NavGraphBuilder.cartScreen(
    navController: NavHostController,
    rootNavController: NavHostController,
    bottomNavigationBarData: @Composable () -> BottomNavigationBarData
) {
    composable<CartRoute> {
        val viewModel = koinViewModel<CartViewModel>()
        CartScreen(
            onEvent = viewModel::setEvent,
            state = viewModel.uiState.collectAsStateWithLifecycle().value,
            effect = viewModel.uiEffect,
            onNavigationEffect = { navigationEffect ->
                when (navigationEffect) {
                    CartContract.Effect.Navigation.GoBack -> navController.navigateUp()
                    CartContract.Effect.Navigation.GoToMarketplaceScreen -> navController.navigateToMarketplaceScreen()

                    is CartContract.Effect.Navigation.GoToListingDetailsScreen -> rootNavController.navigateToListingDetailsScreen(
                        navigationEffect.listingId
                    )

                    CartContract.Effect.Navigation.GoToCheckoutScreen -> rootNavController.navigate(
                        Screen.CheckoutWizardScreen.route
                    ) { launchSingleTop = true }

                    CartContract.Effect.Navigation.GoToEditCheckoutShippingDetailsScreen -> {}
                    CartContract.Effect.Navigation.GoToEditCheckoutPaymentDetailsScreen -> {}
                    is CartContract.Effect.Navigation.GoToCheckoutSuccessScreen -> {}
                }
            },
            bottomNavigationBarData = bottomNavigationBarData
        )
    }
}