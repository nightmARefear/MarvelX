package com.alexgreece45.marvelx.ui.screens.profile.navigation

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarNavigationPayload
import com.alexgreece45.marvelx.ui.helper.getDeepLinkBasePath
import com.alexgreece45.marvelx.ui.helper.openDeepLinkAction
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

fun NavController.navigateToProfileScreen() = navigate(route = ProfileRoute())

internal fun NavGraphBuilder.profileScreen(
    navController: NavHostController
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
            snackbarNavigationPayloadFlow = navBackStackEntry.savedStateHandle
                .getStateFlow<SnackbarNavigationPayload?>(
                    "snackbarNavigationPayload",
                    null
                ),
            onSnackbarNavigationPayloadConsumed = {
                navBackStackEntry.savedStateHandle.remove<SnackbarNavigationPayload>("SnackbarNavigationPayload")
            },
            bottomNavigationBarData = { getBottomNavigationBarData(navController = navController) },
            onNavigation = { navigationEffect ->
                when (navigationEffect) {
                    ProfileContract.Effect.Navigation.NavigateUp -> navController.navigateUp()

                    is ProfileContract.Effect.Navigation.NavigateToProfileImageScreen -> {
                        navController.navigate(
                            Screen.ProfileImageScreen.createNavRoute(navigationEffect.profileImageUrl)
                        )
                    }

                    is ProfileContract.Effect.Navigation.NavigateToProfileDetailsScreen -> {
                        navController.navigate(
                            Screen.ProfileDetailsScreen.createNavRoute(
                                userId = navigationEffect.userId
                            )
                        )
                    }

                    is ProfileContract.Effect.Navigation.NavigateToQrCodeScreen -> {
                        navController.navigate(
                            Screen.QrCodeScreen.createNavRoute(
                                navigationEffect.previousScreenRoute
                            )
                        )
                    }

                    ProfileContract.Effect.Navigation.NavigateToSettingsScreen -> {
                        navController.navigate(Screen.SettingsScreen.route)
                    }

                    ProfileContract.Effect.Navigation.NavigateToFriendsScreen -> {
                        navController.navigate(Screen.FriendsScreen.route)
                    }

                    ProfileContract.Effect.Navigation.NavigateToMyListingsScreen -> {
                        navController.navigate(Screen.MyListingsScreen.route)
                    }

                    ProfileContract.Effect.Navigation.NavigateToOrdersScreen -> {
                        navController.navigate(Screen.OrdersScreen.route)
                    }
                }
            },
            openDeepLinkAction = navController::openDeepLinkAction
        )
    }
}