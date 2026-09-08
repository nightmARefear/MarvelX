package com.alexgreece45.marvelx.ui.screens.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.alexgreece45.marvelx.common_ui.util.ObserveUiEffect
import com.alexgreece45.marvelx.core.common.util.flows.single_observer_flow.SingleObserverFlow
import com.alexgreece45.marvelx.ui.helper.openDeepLinkAction
import com.alexgreece45.marvelx.ui.navigation.getBottomNavigationBarData
import com.alexgreece45.marvelx.ui.screens.allCharacters.navigation.allCharactersScreen
import com.alexgreece45.marvelx.ui.screens.allFavoriteCharacters.navigation.allFavoriteCharactersScreen
import com.alexgreece45.marvelx.ui.screens.allFavoriteCharacters.navigation.navigateToAllFavoriteCharactersScreen
import com.alexgreece45.marvelx.ui.screens.cart.navigation.cartScreen
import com.alexgreece45.marvelx.ui.screens.cart.navigation.navigateToCartScreen
import com.alexgreece45.marvelx.ui.screens.character.navigation.navigateToCharacterScreen
import com.alexgreece45.marvelx.ui.screens.dashboard.model.DashboardContract
import com.alexgreece45.marvelx.ui.screens.marketplace.listingDetailsScreen.navigation.navigateToListingDetailsScreen
import com.alexgreece45.marvelx.ui.screens.marketplace.navigation.MarketplaceRoute
import com.alexgreece45.marvelx.ui.screens.marketplace.navigation.marketplaceScreen
import com.alexgreece45.marvelx.ui.screens.profile.navigation.profileScreen
import com.alexgreece45.marvelx.util.extensions.getAndRemovePendingDeepLinkAction


@Composable
fun DashboardScreen(
    onEvent: (DashboardContract.Event) -> Unit,
    effect: SingleObserverFlow<DashboardContract.Effect>,
    rootNavController: NavHostController,
) {
    val context = LocalContext.current
    val dashboardNavController = rememberNavController()

    LaunchedEffect(Unit) {
        onEvent(DashboardContract.Event.Init(context.getAndRemovePendingDeepLinkAction()))
    }

    ObserveUiEffect(flow = effect, tag = "DashboardScreen") { uiEffect ->
        when (uiEffect) {
            is DashboardContract.Effect.OpenDeepLinkAction -> rootNavController.openDeepLinkAction(
                uiEffect.deepLinkAction
            )
        }
    }

    NavHost(
        navController = dashboardNavController,
        startDestination = MarketplaceRoute
    ) {
        marketplaceScreen(
            navigateToListingDetailsScreen = rootNavController::navigateToListingDetailsScreen,
            navigateToFavoritesScreen = dashboardNavController::navigateToAllFavoriteCharactersScreen,
            navigateToCartScreen = dashboardNavController::navigateToCartScreen,
            bottomNavigationBarData = { getBottomNavigationBarData(dashboardNavController) },
        )

        allCharactersScreen(
            navController = dashboardNavController,
            navigateToCharacterScreen = { characterId ->
                rootNavController.navigateToCharacterScreen(
                    characterId = characterId,
                    navOptionsBuilder = {
                        navOptions {
                            launchSingleTop = true
                        }
                    }
                )
            },
            openDeepLinkAction = rootNavController::openDeepLinkAction
        )

        allFavoriteCharactersScreen(dashboardNavController)

        cartScreen(
            navController = dashboardNavController,
            rootNavController = rootNavController,
            bottomNavigationBarData = { getBottomNavigationBarData(dashboardNavController) }
        )

        profileScreen(
            navController = dashboardNavController,
            rootNavController = rootNavController
        )
    }
}