package com.alexgreece45.marvelx.ui.navigation.marketplace

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.alexgreece45.marvelx.ui.components.BottomNavigationBarData
import com.alexgreece45.marvelx.ui.navigation.NavItem
import com.alexgreece45.marvelx.ui.navigation.Screen
import com.alexgreece45.marvelx.ui.screens.marketplace.MarketplaceScreen
import com.alexgreece45.marvelx.ui.screens.marketplace.MarketplaceViewModel
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.CreateListingWizardScreen
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.CreateListingWizardViewModel
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.CreateListingWizardContract
import com.alexgreece45.marvelx.ui.screens.marketplace.listingDetailsScreen.ListingDetailsScreen
import com.alexgreece45.marvelx.ui.screens.marketplace.listingDetailsScreen.ListingDetailsViewModel
import com.alexgreece45.marvelx.ui.screens.marketplace.listingDetailsScreen.model.ListingDetailsContract
import com.alexgreece45.marvelx.ui.screens.marketplace.model.MarketplaceContract
import com.alexgreece45.marvelx.util.extensions.composable
import org.koin.androidx.compose.koinViewModel


fun NavGraphBuilder.marketplaceNavigation(
    navController: NavHostController,
    bottomNavigationBarData: @Composable () -> BottomNavigationBarData,
) {
    composable(Screen.MarketplaceScreen) {
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
                        navController.navigate(
                            Screen.ListingDetailsScreen.createNavRoute(
                                navigationEffect.listingId
                            )
                        )
                    }
                }
            },
            bottomNavigationBarData = bottomNavigationBarData
        )
    }

    composable(Screen.ListingDetailsScreen) { navBackStackEntry ->
        val viewModel = koinViewModel<ListingDetailsViewModel>()
        ListingDetailsScreen(
            onEvent = viewModel::setEvent,
            state = viewModel.uiState.collectAsStateWithLifecycle().value,
            effect = viewModel.uiEffect,
            onNavigation = { navigationEffect ->
                when (navigationEffect) {
                    ListingDetailsContract.Effect.Navigation.GoBack -> {
                        navController.navigateUp()
                    }

                    is ListingDetailsContract.Effect.Navigation.NavigateToProfileDetailsScreen -> {
                        navController.navigate(
                            Screen.ProfileDetailsScreen.createNavRoute(
                                navigationEffect.userId
                            )
                        )
                    }

                    is ListingDetailsContract.Effect.Navigation.NavigateToEditListingScreen -> {
                        navController.navigate(
                            Screen.CreateListingWizardScreen.createNavRoute(
                                navigationEffect.listingId
                            )
                        )
                    }

                    ListingDetailsContract.Effect.Navigation.GoToNextScreen -> {}
                }
            },
            listingId = navBackStackEntry.arguments?.getString(NavItem.ListingId.key, ""),
            createListingWizardMode = null
        )
    }

    composable(Screen.CreateListingWizardScreen) { navBackStackEntry ->
        val viewModel = koinViewModel<CreateListingWizardViewModel>()
        CreateListingWizardScreen(
            onEvent = viewModel::setEvent,
            state = viewModel.uiState.collectAsStateWithLifecycle().value,
            effect = viewModel.uiEffect,
            onNavigationEffect = { navigationEffect ->
                when (navigationEffect) {
                    CreateListingWizardContract.Effect.Navigation.ExitWizard -> navController.navigateUp()
                    is CreateListingWizardContract.Effect.Navigation.NavigateToListingDetailsScreenAndExitWizard -> {
                        navController.navigate(
                            Screen.ListingDetailsScreen.createNavRoute(
                                navigationEffect.listingId
                            )
                        ) {
                            popUpTo(Screen.ListingDetailsScreen.route) {
                                inclusive = true
                            }
                        }
                    }
                }
            },
            listingId = navBackStackEntry.arguments?.getString(NavItem.ListingId.key)
        )
    }
}
