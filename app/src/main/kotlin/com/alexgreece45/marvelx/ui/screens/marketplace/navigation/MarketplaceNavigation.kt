package com.alexgreece45.marvelx.ui.screens.marketplace.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import com.alexgreece45.marvelx.ui.components.BottomNavigationBarData
import com.alexgreece45.marvelx.ui.screens.marketplace.MarketplaceScreen
import com.alexgreece45.marvelx.ui.screens.marketplace.MarketplaceViewModel
import com.alexgreece45.marvelx.ui.screens.marketplace.model.MarketplaceContract
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
internal data object MarketplaceRoute

fun NavController.navigateToMarketplaceScreen(
    clearBackStack: Boolean = false
) = navigate(
    route = MarketplaceRoute,
    navOptions = navOptions {
        if (clearBackStack) {
            popUpTo<MarketplaceRoute> {
                inclusive = true
            }
        }
    }
)

internal fun NavGraphBuilder.marketplaceScreen(
    navigateToListingDetailsScreen: (String) -> Unit,
    bottomNavigationBarData: @Composable () -> BottomNavigationBarData
) {
    composable<MarketplaceRoute> {
        val viewModel = koinViewModel<MarketplaceViewModel>()
        MarketplaceScreen(
            onEvent = viewModel::setEvent,
            state = viewModel.uiState.collectAsStateWithLifecycle().value,
            effect = viewModel.uiEffect,
            onNavigation = { navigationEffect ->
                when (navigationEffect) {
                    is MarketplaceContract.Effect.Navigation.GoToListingsByCategoryScreen -> {

                    }

                    is MarketplaceContract.Effect.Navigation.GoToListingDetailsScreen -> {
                        navigateToListingDetailsScreen(navigationEffect.listingId)
                    }
                }
            },
            bottomNavigationBarData = bottomNavigationBarData,
            snackbarEffects = viewModel.snackbarEffects
        )
    }
}