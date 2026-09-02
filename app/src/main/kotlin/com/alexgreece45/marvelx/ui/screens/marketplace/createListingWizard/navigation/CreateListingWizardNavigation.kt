package com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.navigation

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import androidx.navigation.toRoute
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.CreateListingWizardScreen
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.CreateListingWizardViewModel
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.CreateListingWizardContract
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
internal data class CreateListingWizardRoute(
    val listingId: String?
)

fun NavController.navigateToCreateListingWizardScreen(listingId: String?) =
    navigate(
        route = CreateListingWizardRoute(listingId),
        navOptions = navOptions { launchSingleTop = true }
    )

fun NavGraphBuilder.createListingWizardScreen(
    goBack: () -> Unit,
    navigateToListingDetailsScreen: (listingId: String, NavOptionsBuilder.() -> Unit) -> Unit,
    navigateToCreateListingSuccessScreen: (listingId: String, wizardMode: String, navOptionsBuilder: NavOptionsBuilder.() -> Unit) -> Unit
) {
    composable<CreateListingWizardRoute> { navBackStackEntry ->
        val route = navBackStackEntry.toRoute<CreateListingWizardRoute>()
        val viewModel = koinViewModel<CreateListingWizardViewModel>()

        CreateListingWizardScreen(
            onEvent = viewModel::setEvent,
            state = viewModel.uiState.collectAsStateWithLifecycle().value,
            effect = viewModel.uiEffect,
            snackbarEffects = viewModel.snackbarEffects,
            onNavigationEffect = { navigationEffect ->
                when (navigationEffect) {
                    CreateListingWizardContract.Effect.Navigation.ExitWizard -> goBack()
                    is CreateListingWizardContract.Effect.Navigation.NavigateToListingDetailsScreen -> {
                        navigateToListingDetailsScreen(
                            navigationEffect.listingId
                        ) {
                            popUpTo(route) { inclusive = true }
                        }
                    }

                    is CreateListingWizardContract.Effect.Navigation.NavigateToCreateListingSuccessScreen -> {
                        navigateToCreateListingSuccessScreen(
                            navigationEffect.listingId,
                            navigationEffect.wizardMode.name
                        ) {
                            popUpTo(route) { inclusive = true }
                        }
                    }
                }
            }
        )
    }
}