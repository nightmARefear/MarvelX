package com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.success.navigation

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import androidx.navigation.toRoute
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.navigation.CreateListingWizardRoute
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.success.CreateListingSuccessScreen
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.success.CreateListingSuccessViewModel
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.success.model.CreateListingSuccessContract
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
private data class CreateListingSuccessRoute(
    val listingId: String,
    val wizardMode: String
)

fun NavController.navigateToCreateListingSuccessScreen(
    listingId: String,
    wizardMode: String
) = navigate(
    route = CreateListingSuccessRoute(listingId, wizardMode),
    navOptions = navOptions {
        popUpTo<CreateListingSuccessRoute> {
            inclusive = true
        }
    }
)

internal fun NavGraphBuilder.createListingSuccessScreen(
    navigateToListingDetailsScreen: (String) -> Unit,
    exitWizard: () -> Unit
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
                    is CreateListingSuccessContract.Effect.Navigation.NavigateToListingDetailsScreen -> navigateToListingDetailsScreen(
                        navigationEffect.listingId.value
                    )

                    CreateListingSuccessContract.Effect.Navigation.GoBack -> exitWizard()
                }
            },
            listingId = createListingSuccessRoute.listingId,
            wizardMode = createListingSuccessRoute.wizardMode,
        )
    }
}