package com.alexgreece45.marvelx.ui.screens.marketplace.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import com.alexgreece45.marvelx.ui.components.BottomNavigationBarData
import com.alexgreece45.marvelx.ui.navigation.Screen
import com.alexgreece45.marvelx.ui.screens.favoriteListings.navigation.navigateToFavoriteListingsScreen
import com.alexgreece45.marvelx.ui.screens.cart.navigation.navigateToCartScreen
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.navigation.createListingWizardScreen
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.navigation.navigateToCreateListingWizardScreen
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.success.navigation.createListingSuccessScreen
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.success.navigation.navigateToCreateListingSuccessScreen
import com.alexgreece45.marvelx.ui.screens.marketplace.listingDetailsScreen.navigation.listingDetailsScreen
import com.alexgreece45.marvelx.ui.screens.marketplace.listingDetailsScreen.navigation.navigateToListingDetailsScreen
import com.alexgreece45.marvelx.ui.screens.orders.orderItemManagement.navigation.navigateToOrderItemManagementScreen
import com.alexgreece45.marvelx.ui.screens.orders.orderItemTrackingHistory.navigation.navigateToOrderItemTrackingHistoryScreen
import kotlinx.serialization.Serializable

@Serializable
data object MarketplaceGraphRoute

fun NavGraphBuilder.marketplaceGraph(
    navController: NavHostController,
    bottomNavigationBarData: @Composable () -> BottomNavigationBarData,
) {
    navigation<MarketplaceGraphRoute>(startDestination = MarketplaceRoute) {
        marketplaceScreen(
            navigateToListingDetailsScreen = navController::navigateToListingDetailsScreen,
            navigateToFavoritesScreen = navController::navigateToFavoriteListingsScreen,
            navigateToCartScreen = navController::navigateToCartScreen,
            bottomNavigationBarData = bottomNavigationBarData,
        )

        // TODO: May need to be moved to another graph.
        listingDetailsScreen(
            goBack = navController::navigateUp,
            navigateToProfileDetailsScreen = { userId ->
                navController.navigate(
                    Screen.ProfileDetailsScreen.createNavRoute(
                        userId = userId
                    )
                )
            },
            navigateToEditListingScreen = navController::navigateToCreateListingWizardScreen,
            navigateToOrderItemManagementScreen = navController::navigateToOrderItemManagementScreen,
            navigateToOrderItemTrackingHistoryScreen = navController::navigateToOrderItemTrackingHistoryScreen
        )

        createListingWizardScreen(
            goBack = navController::navigateUp,
            navigateToListingDetailsScreen = navController::navigateToListingDetailsScreen,
            navigateToCreateListingSuccessScreen = navController::navigateToCreateListingSuccessScreen
        )

        createListingSuccessScreen(navController = navController)
    }
}