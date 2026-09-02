package com.alexgreece45.marvelx.ui.screens.profile.navigation

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarNavigationPayload
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarType
import com.alexgreece45.marvelx.ui.helper.getDeepLinkBasePath
import com.alexgreece45.marvelx.ui.helper.openDeepLinkAction
import com.alexgreece45.marvelx.ui.navigation.SavedStateHandleKeys
import com.alexgreece45.marvelx.ui.navigation.Screen
import com.alexgreece45.marvelx.ui.navigation.getBottomNavigationBarData
import com.alexgreece45.marvelx.ui.screens.profile.ProfileScreen
import com.alexgreece45.marvelx.ui.screens.profile.ProfileViewModel
import com.alexgreece45.marvelx.ui.screens.profile.model.ProfileContract
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

private const val screenName = "profileScreen"

@Serializable
data class ProfileRoute(val qrCodeScanErrorMessage: String? = null)

internal fun NavGraphBuilder.profileScreen(
    navController: NavHostController,
    rootNavController: NavHostController
) {
    composable<ProfileRoute>(
        deepLinks = listOf(
            navDeepLink<ProfileRoute>(basePath = getDeepLinkBasePath(screenName))
        )
    ) { navBackStackEntry ->
        val viewModel = koinViewModel<ProfileViewModel>()
        ProfileScreen(
            onEventSent = viewModel::setEvent,
            state = viewModel.uiState.collectAsStateWithLifecycle().value,
            effect = viewModel.uiEffect,
            snackbarEffects = viewModel.snackbarEffects,
            snackbarNavigationPayloadFlow = navBackStackEntry.savedStateHandle
                .getStateFlow<SnackbarNavigationPayload?>(
                    key = SavedStateHandleKeys.snackbarNavigationPayload,
                    initialValue = null
                ),
            onSnackbarNavigationPayloadConsumed = {
                navBackStackEntry.savedStateHandle.remove<SnackbarNavigationPayload>(
                    SavedStateHandleKeys.snackbarNavigationPayload
                )
            },
            bottomNavigationBarData = { getBottomNavigationBarData(navController = navController) },
            onNavigation = { navigationEffect ->
                when (navigationEffect) {
                    ProfileContract.Effect.Navigation.NavigateUp -> rootNavController.navigateUp()

                    is ProfileContract.Effect.Navigation.NavigateToProfileImageScreen -> {
                        rootNavController.navigate(
                            Screen.ProfileImageScreen.createNavRoute(navigationEffect.profileImageUrl)
                        )
                    }

                    is ProfileContract.Effect.Navigation.NavigateToProfileDetailsScreen -> {
                        rootNavController.navigate(
                            Screen.ProfileDetailsScreen.createNavRoute(
                                userId = navigationEffect.userId
                            )
                        )
                    }

                    is ProfileContract.Effect.Navigation.NavigateToQrCodeScreen -> {
                        rootNavController.navigate(
                            Screen.QrCodeScreen.createNavRoute(
                                navigationEffect.previousScreenRoute
                            )
                        )
                    }

                    ProfileContract.Effect.Navigation.NavigateToSettingsScreen -> {
                        rootNavController.navigate(Screen.SettingsScreen.route)
                    }

                    ProfileContract.Effect.Navigation.NavigateToFriendsScreen -> {
                        rootNavController.navigate(Screen.FriendsScreen.route)
                    }

                    ProfileContract.Effect.Navigation.NavigateToMyListingsScreen -> {
                        rootNavController.navigate(Screen.MyListingsScreen.route)
                    }

                    ProfileContract.Effect.Navigation.NavigateToOrdersScreen -> {
                        rootNavController.navigate(Screen.OrdersScreen.route)
                    }
                }
            },
            openDeepLinkAction = rootNavController::openDeepLinkAction
        )
    }
}