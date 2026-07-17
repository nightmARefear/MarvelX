package com.alexgreece45.marvelx.ui.screens.marketplace.listingDetailsScreen.navigation

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navOptions
import androidx.navigation.toRoute
import com.alexgreece45.marvelx.ui.helper.getDeepLinkBasePath
import com.alexgreece45.marvelx.ui.screens.marketplace.listingDetailsScreen.ListingDetailsScreen
import com.alexgreece45.marvelx.ui.screens.marketplace.listingDetailsScreen.ListingDetailsViewModel
import com.alexgreece45.marvelx.ui.screens.marketplace.listingDetailsScreen.model.ListingDetailsContract
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

private const val screenName = "listingDetailsScreen"

@Serializable
private data class ListingDetailsRoute(
    val listingId: String? = ""
)

fun NavController.navigateToListingDetailsScreen(
    listingId: String,
    clearBackStack: Boolean = false
) = navigate(
    route = ListingDetailsRoute(listingId),
    navOptions = navOptions {
        if (clearBackStack) {
            popUpTo<ListingDetailsRoute> {
                inclusive = true
            }
        }
    }
)

internal fun NavGraphBuilder.listingDetailsScreen(
    goBack: () -> Unit,
    navigateToProfileDetailsScreen: (String) -> Unit,
    navigateToEditListingScreen: (String) -> Unit
) {
    composable<ListingDetailsRoute>(
        deepLinks = listOf(
            navDeepLink<ListingDetailsRoute>(basePath = getDeepLinkBasePath(screenName))
        )
    ) { navBackStackEntry ->
        val route = navBackStackEntry.toRoute<ListingDetailsRoute>()
        val viewModel = koinViewModel<ListingDetailsViewModel>()

        ListingDetailsScreen(
            onEvent = viewModel::setEvent,
            state = viewModel.uiState.collectAsStateWithLifecycle().value,
            effect = viewModel.uiEffect,
            onNavigation = { navigationEffect ->
                when (navigationEffect) {
                    ListingDetailsContract.Effect.Navigation.GoBack -> goBack()

                    is ListingDetailsContract.Effect.Navigation.NavigateToProfileDetailsScreen -> navigateToProfileDetailsScreen(
                        navigationEffect.userId.value
                    )

                    is ListingDetailsContract.Effect.Navigation.NavigateToEditListingScreen -> navigateToEditListingScreen(
                        navigationEffect.listingId
                    )

                    ListingDetailsContract.Effect.Navigation.GoToNextScreen -> {}
                }
            },
            listingId = route.listingId,
            createListingWizardMode = null,
            isInnerScreen = false
        )
    }
}