package com.alexgreece45.marvelx.ui.navigation.cart

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.alexgreece45.marvelx.ui.components.BottomNavigationBarData
import com.alexgreece45.marvelx.ui.navigation.Screen
import com.alexgreece45.marvelx.ui.screens.cart.CartScreen
import com.alexgreece45.marvelx.ui.screens.cart.CartViewModel
import com.alexgreece45.marvelx.ui.screens.cart.model.CartContract
import com.alexgreece45.marvelx.util.extensions.composable
import org.koin.androidx.compose.koinViewModel

fun NavGraphBuilder.cartNavigation(
    navController: NavHostController,
    bottomNavigationBarData: @Composable () -> BottomNavigationBarData
) {
    composable(screen = Screen.CartScreen) {
        val viewModel = koinViewModel<CartViewModel>()
        CartScreen(
            onEvent = viewModel::setEvent,
            state = viewModel.uiState.collectAsStateWithLifecycle().value,
            effect = viewModel.uiEffect,
            onNavigationEffect = { navigationEffect ->
                when (navigationEffect) {
                    CartContract.Effect.Navigation.GoBack -> navController.navigateUp()
                    CartContract.Effect.Navigation.GoToMarketplaceScreen -> navController.navigate(
                        Screen.CartScreen.route
                    )

                    is CartContract.Effect.Navigation.GoToListingDetailsScreen -> navController.navigate(
                        Screen.ListingDetailsScreen.createNavRoute(navigationEffect.listingId)
                    )

                    CartContract.Effect.Navigation.GoToCheckoutScreen -> navController.navigate(
                        Screen.CheckoutWizardScreen.route
                    )

                    CartContract.Effect.Navigation.GoToEditCheckoutShippingDetailsScreen -> {}
                    CartContract.Effect.Navigation.GoToEditCheckoutPaymentDetailsScreen -> {}
                    is CartContract.Effect.Navigation.GoToCheckoutSuccessScreen -> {}
                }
            },
            bottomNavigationBarData = bottomNavigationBarData,
            orderCheckoutInfo = null
        )
    }
}
