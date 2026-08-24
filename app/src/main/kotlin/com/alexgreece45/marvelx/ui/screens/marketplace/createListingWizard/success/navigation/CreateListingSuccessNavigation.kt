package com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.success.navigation

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import androidx.navigation.toRoute
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.success.CreateListingSuccessScreen
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.success.CreateListingSuccessViewModel
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.success.model.CreateListingSuccessContract
import com.alexgreece45.marvelx.ui.screens.marketplace.listingDetailsScreen.navigation.ListingDetailsRoute
import com.alexgreece45.marvelx.ui.screens.marketplace.listingDetailsScreen.navigation.navigateToListingDetailsScreen
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
internal data class CreateListingSuccessRoute(
    val listingId: String,
    val wizardMode: String
)

fun NavController.navigateToCreateListingSuccessScreen(
    listingId: String,
    wizardMode: String,
    navOptionsBuilder: (NavOptionsBuilder.() -> Unit)?
) {
    val route = CreateListingSuccessRoute(listingId, wizardMode)
    navigate(
        route = route,
        navOptions = navOptions {
            navOptionsBuilder?.let { it() }
            launchSingleTop = true
        }
    )
}

internal fun NavGraphBuilder.createListingSuccessScreen(
    navController: NavController
) {
    composable<CreateListingSuccessRoute> { navBackStackEntry ->
        val createListingSuccessRoute = navBackStackEntry.toRoute<CreateListingSuccessRoute>()
        val viewModel = koinViewModel<CreateListingSuccessViewModel>()

        CreateListingSuccessScreen(
            onEvent = viewModel::setEvent,
            state = viewModel.uiState.collectAsStateWithLifecycle().value,
            effect = viewModel.uiEffect,
            onNavigationEffect = { navigationEffect ->
                when (navigationEffect) {
                    is CreateListingSuccessContract.Effect.Navigation.NavigateToListingDetailsScreenAndPopUpToSuccess -> navController.navigateToListingDetailsScreen(
                        navigationEffect.listingId.value
                    ) { popUpTo(createListingSuccessRoute) { inclusive = true } }

                    is CreateListingSuccessContract.Effect.Navigation.NavigateAndPopUpToListingDetailsScreen -> navController.navigateToListingDetailsScreen(
                        navigationEffect.listingId.value
                    ) {
                        popUpTo(ListingDetailsRoute(navigationEffect.listingId.value)) {
                            inclusive = true
                        }
                    }

                    CreateListingSuccessContract.Effect.Navigation.GoBack -> navController.navigateUp()
                }
            }
        )
    }
}