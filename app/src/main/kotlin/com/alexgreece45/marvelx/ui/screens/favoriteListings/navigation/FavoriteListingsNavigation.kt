package com.alexgreece45.marvelx.ui.screens.favoriteListings.navigation

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.alexgreece45.marvelx.ui.navigation.getBottomNavigationBarData
import com.alexgreece45.marvelx.ui.screens.favoriteListings.FavoriteListingsScreen
import com.alexgreece45.marvelx.ui.screens.favoriteListings.FavoriteListingsViewModel
import com.alexgreece45.marvelx.ui.screens.favoriteListings.model.FavoriteListingsContract
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
data object FavoriteListingsRoute

fun NavController.navigateToFavoriteListingsScreen() = navigate(FavoriteListingsRoute)

internal fun NavGraphBuilder.favoriteListingsScreen(
    navController: NavController,
    navigateToListingDetailsScreen: (listingId: String) -> Unit
) {
    composable<FavoriteListingsRoute> {
        val viewModel = koinViewModel<FavoriteListingsViewModel>()

        FavoriteListingsScreen(
            onEvent = viewModel::setEvent,
            state = viewModel.uiState.collectAsStateWithLifecycle().value,
            effect = viewModel.uiEffect,
            snackbarEffects = viewModel.snackbarEffects,
            onNavigationEffect = { navigationEffect ->
                when (navigationEffect) {
                    is FavoriteListingsContract.Effect.Navigation.GoToListingDetailsScreen -> {
                        navigateToListingDetailsScreen(navigationEffect.listingId)
                    }
                }
            },
            bottomNavigationBarData = { getBottomNavigationBarData(navController = navController) }
        )
    }
}
