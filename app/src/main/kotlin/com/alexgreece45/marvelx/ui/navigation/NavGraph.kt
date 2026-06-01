package com.alexgreece45.marvelx.ui.navigation

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.components.BottomBarItem
import com.alexgreece45.marvelx.ui.components.BottomNavigationBarData
import com.alexgreece45.marvelx.ui.components.IconData
import com.alexgreece45.marvelx.ui.helper.openDeepLinkAction
import com.alexgreece45.marvelx.ui.navigation.cart.cartNavigation
import com.alexgreece45.marvelx.ui.navigation.marketplace.marketplaceNavigation
import com.alexgreece45.marvelx.ui.screens.allCharacters.AllCharactersScreen
import com.alexgreece45.marvelx.ui.screens.allCharacters.AllCharactersViewModel
import com.alexgreece45.marvelx.ui.screens.allFavoriteCharacters.AllFavoriteCharactersScreen
import com.alexgreece45.marvelx.ui.screens.allFavoriteCharacters.AllFavoriteCharactersViewModel
import com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.CheckoutWizardScreen
import com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.CheckoutWizardViewModel
import com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.model.CheckoutWizardContract
import com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.steps.shippingDetails.CheckoutShippingDetailsViewModel
import com.alexgreece45.marvelx.ui.screens.changePassword.ChangePasswordScreen
import com.alexgreece45.marvelx.ui.screens.character.CharacterScreen
import com.alexgreece45.marvelx.ui.screens.character.CharacterViewModel
import com.alexgreece45.marvelx.ui.screens.character.allCharacterComicsScreen.AllCharacterComicsScreen
import com.alexgreece45.marvelx.ui.screens.character.allCharacterComicsScreen.AllCharacterComicsViewModel
import com.alexgreece45.marvelx.ui.screens.comic.ComicScreen
import com.alexgreece45.marvelx.ui.screens.comic.ComicViewModel
import com.alexgreece45.marvelx.ui.screens.forgotPasswordScreen.ForgotPasswordScreen
import com.alexgreece45.marvelx.ui.screens.forgotPasswordScreen.forgotPasswordValidateEmailScreen.ForgotPasswordValidateEmailScreen
import com.alexgreece45.marvelx.ui.screens.friends.FriendsScreen
import com.alexgreece45.marvelx.ui.screens.friends.FriendsViewModel
import com.alexgreece45.marvelx.ui.screens.friends.model.FriendsContract
import com.alexgreece45.marvelx.ui.screens.home.HomeEffect
import com.alexgreece45.marvelx.ui.screens.home.HomeScreen
import com.alexgreece45.marvelx.ui.screens.home.HomeViewModel
import com.alexgreece45.marvelx.ui.screens.introLoading.IntroLoadingScreen
import com.alexgreece45.marvelx.ui.screens.introLoading.IntroLoadingViewModel
import com.alexgreece45.marvelx.ui.screens.login.LoginScreen
import com.alexgreece45.marvelx.ui.screens.login.LoginViewModel
import com.alexgreece45.marvelx.ui.screens.myListings.MyListingsScreen
import com.alexgreece45.marvelx.ui.screens.myListings.MyListingsViewModel
import com.alexgreece45.marvelx.ui.screens.myListings.model.MyListingsContract
import com.alexgreece45.marvelx.ui.screens.orders.OrdersScreen
import com.alexgreece45.marvelx.ui.screens.orders.OrdersViewModel
import com.alexgreece45.marvelx.ui.screens.orders.model.OrdersContract
import com.alexgreece45.marvelx.ui.screens.orders.orderDetails.OrderDetailsScreen
import com.alexgreece45.marvelx.ui.screens.orders.orderDetails.OrderDetailsViewModel
import com.alexgreece45.marvelx.ui.screens.orders.orderDetails.model.OrderDetailsContract
import com.alexgreece45.marvelx.ui.screens.orders.orderItemTrackingHistory.OrderItemTrackingHistoryScreen
import com.alexgreece45.marvelx.ui.screens.orders.orderItemTrackingHistory.OrderItemTrackingHistoryViewModel
import com.alexgreece45.marvelx.ui.screens.orders.orderItemTrackingHistory.model.OrderItemTrackingHistoryContract
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.RequestItemReturnScreen
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.RequestItemReturnViewModel
import com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model.RequestItemReturnContract
import com.alexgreece45.marvelx.ui.screens.otp.OtpScreen
import com.alexgreece45.marvelx.ui.screens.otp.OtpViewModel
import com.alexgreece45.marvelx.ui.screens.profile.ProfileScreen
import com.alexgreece45.marvelx.ui.screens.profile.ProfileViewModel
import com.alexgreece45.marvelx.ui.screens.profile.editProfileDetails.EditProfileDetailsScreen
import com.alexgreece45.marvelx.ui.screens.profile.editProfileDetails.EditProfileDetailsViewModel
import com.alexgreece45.marvelx.ui.screens.profile.image.ProfileImageScreen
import com.alexgreece45.marvelx.ui.screens.profile.image.ProfileImageViewModel
import com.alexgreece45.marvelx.ui.screens.profile.model.ProfileContract
import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.ProfileDetailsContract
import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.ProfileDetailsScreen
import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.ProfileDetailsViewModel
import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.stats.ProfileDetailsStatsScreen
import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.stats.ProfileDetailsStatsViewModel
import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.stats.model.ProfileDetailsStatsContract
import com.alexgreece45.marvelx.ui.screens.qrCode.QrCodeScanContract
import com.alexgreece45.marvelx.ui.screens.qrCode.QrCodeScanScreen
import com.alexgreece45.marvelx.ui.screens.qrCode.QrCodeScanViewModel
import com.alexgreece45.marvelx.ui.screens.settings.SettingsScreen
import com.alexgreece45.marvelx.ui.screens.settings.SettingsViewModel
import com.alexgreece45.marvelx.ui.screens.settings.faq.FaqScreen
import com.alexgreece45.marvelx.ui.screens.settings.faq.FaqViewModel
import com.alexgreece45.marvelx.ui.screens.settings.model.SettingsContract
import com.alexgreece45.marvelx.ui.screens.settings.privacySettings.PrivacySettingsScreen
import com.alexgreece45.marvelx.ui.screens.settings.privacySettings.PrivacySettingsViewModel
import com.alexgreece45.marvelx.ui.screens.signup.SignUpScreen
import com.alexgreece45.marvelx.ui.screens.signup.SignupViewModel
import com.alexgreece45.marvelx.ui.screens.splash.SplashScreen
import com.alexgreece45.marvelx.ui.theme.values.customColors
import com.alexgreece45.marvelx.ui.transitions.EnterTransition
import com.alexgreece45.marvelx.ui.transitions.ExitTransition
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.extensions.composable
import com.alexgreece45.marvelx.util.extensions.openAppSettings
import com.alexgreece45.marvelx.util.extensions.popUpToTop
import org.koin.androidx.compose.koinViewModel

/**
 * Generates the `NavHost` of this application by providing all the necessary routes / screens.
 *
 * @param navController The root [NavHostController] to be used by the `NavHost`.
 */
@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.getStartDestination()
    ) {
        // Register screens.
        composable(Screen.SplashScreen) {
            SplashScreen(
                popUpToHomeScreen = {
                    navController.navigate(Screen.HomeScreen.route) {
                        popUpToTop(navController)
                    }
                },
                popUpToIntroLoadingScreen = {
                    navController.navigate(Screen.IntroLoadingScreen.route) {
                        popUpToTop(navController)
                    }
                }
            )
        }

        composable(Screen.IntroLoadingScreen) {
            val viewModel = koinViewModel<IntroLoadingViewModel>()
            IntroLoadingScreen(
                navController = navController,
                onEventSent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect
            )
        }

        composable(Screen.HomeScreen) { navBackStackEntry ->
            val viewModel = koinViewModel<HomeViewModel>()
            HomeScreen(
                onEventSent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                logoutReason = navBackStackEntry.arguments?.getString(NavItem.LogoutReason.key),
                onNavigationEffect = { navigationEffect ->
                    when (navigationEffect) {
                        HomeEffect.Navigation.PopBackStack -> navController.navigateUp()
                        HomeEffect.Navigation.NavigateToLoginScreen -> {
                            navController.navigate(Screen.LoginScreen.route) {
                                launchSingleTop = true
                            }
                        }

                        HomeEffect.Navigation.NavigateToSignupScreen -> {
                            navController.navigate(Screen.SignupScreen.route) {
                                launchSingleTop = true
                            }
                        }

                        is HomeEffect.Navigation.NavigateToOtpScreen -> {
                            with(navigationEffect) {
                                navController.navigate(
                                    Screen.OtpScreen.createNavRoute(
                                        userId = userId,
                                        otpFlowType = otpFlowType,
                                        verificationStep = verificationStep,
                                        shouldRememberUserSession = shouldRememberUserSession
                                    )
                                )
                            }
                        }

                        HomeEffect.Navigation.NavigateToAllCharactersScreen -> {
                            navController.navigate(Screen.AllCharactersScreen.route) {
                                popUpToTop(navController)
                            }
                        }
                    }
                }
            )
        }

        composable(Screen.LoginScreen) {
            val viewModel = koinViewModel<LoginViewModel>()
            LoginScreen(
                onEventSent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                navigateToSignupScreen = {
                    navController.navigate(Screen.SignupScreen.route) {
                        launchSingleTop = true
                    }
                },
                navigateToAllCharactersScreen = {
                    navController.navigate(Screen.AllCharactersScreen.route) {
                        popUpToTop(navController)
                    }
                },
                navigateToOtpScreen = { userId, otpFlowType, verificationStep, shouldRememberUserSession ->
                    navController.navigate(
                        Screen.OtpScreen.createNavRoute(
                            userId = userId,
                            otpFlowType = otpFlowType,
                            verificationStep = verificationStep,
                            shouldRememberUserSession = shouldRememberUserSession
                        )
                    )
                },
                navigateToForgotPasswordScreen = {
                    navController.navigate(Screen.ForgotPasswordScreen.route)
                },
                popBackStack = navController::navigateUp
            )
        }

        composable(Screen.ForgotPasswordScreen) {
            ForgotPasswordScreen(
                popUpToLoginScreen = navController::navigateUp,
                navController = navController
            )
        }

        composable(Screen.ForgotPasswordValidateEmailScreen) { navBackStackEntry ->
            ForgotPasswordValidateEmailScreen(
                popBackStack = navController::navigateUp,
                navigateToForgotPasswordEnterPhoneNumberScreen = { requestIdEmail ->
                    navController.navigate(
                        Screen.ForgotPasswordEnterPhoneNumberScreen.createNavRoute(
                            requestIdEmail = requestIdEmail
                        )
                    )
                },
                requestIdEmail = navBackStackEntry.arguments?.getString(NavItem.RequestIdEmail.key)
            )
        }

        composable(Screen.ChangePasswordScreen) { entry ->
            ChangePasswordScreen(
                popBackStack = navController::navigateUp,
                otpFlowType = entry.arguments?.getString(NavItem.OtpFlowType.key),
                requestIdEmail = entry.arguments?.getString(NavItem.RequestIdEmail.key).orEmpty(),
                requestIdPhoneNumber = entry.arguments?.getString(NavItem.RequestIdPhoneNumber.key)
            )
        }

        composable(Screen.SignupScreen) {
            val viewModel = koinViewModel<SignupViewModel>()
            SignUpScreen(
                onEventSent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                navigateToAllCharactersScreen = {
                    navController.navigate(Screen.AllCharactersScreen.route) {
                        popUpToTop(navController)
                    }
                },
                navigateToOtpScreen = { userId, otpFlowType, verificationStep, shouldRememberUserSession ->
                    navController.navigate(
                        Screen.OtpScreen.createNavRoute(
                            userId = userId,
                            otpFlowType = otpFlowType,
                            verificationStep = verificationStep,
                            shouldRememberUserSession = shouldRememberUserSession
                        )
                    )
                },
                popBackStack = navController::navigateUp
            )
        }

        composable(Screen.OtpScreen) { navBackStackEntry ->
            val viewModel = koinViewModel<OtpViewModel>()
            OtpScreen(
                onEventSent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                popBackStack = navController::navigateUp,
                navigateToAllCharactersScreen = {
                    navController.navigate(Screen.AllCharactersScreen.route) {
                        popUpToTop(navController)
                    }
                },
                navigateToChangePasswordScreen = { requestIdEmail, requestIdPhoneNumber ->
                    navController.navigate(
                        Screen.ChangePasswordScreen.createNavRoute(
                            requestIdEmail = requestIdEmail,
                            requestIdPhoneNumber = requestIdPhoneNumber
                        )
                    )
                },
                userId = navBackStackEntry.arguments?.getString(NavItem.UserId.key),
                otpFlowType = navBackStackEntry.arguments?.getString(NavItem.OtpFlowType.key),
                verificationStep = navBackStackEntry.arguments?.getString(NavItem.VerificationStep.key),
                shouldRememberUserSession = navBackStackEntry.arguments?.getBoolean(NavItem.ShouldRememberUserSession.key),
                requestIdEmail = navBackStackEntry.arguments?.getString(NavItem.RequestIdEmail.key),
                requestIdPhoneNumber = navBackStackEntry.arguments?.getString(NavItem.RequestIdPhoneNumber.key)
            )
        }

        // Main screens.
        marketplaceNavigation(
            navController,
            bottomNavigationBarData = { getBottomNavigationBarData(navController) }
        )

        composable(screen = Screen.MyListingsScreen) {
            val viewModel = koinViewModel<MyListingsViewModel>()
            MyListingsScreen(
                onEvent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                onNavigationEffect = { navigationEffect ->
                    when (navigationEffect) {
                        MyListingsContract.Effect.Navigation.GoBack -> navController.navigateUp()
                        MyListingsContract.Effect.Navigation.GoToCreateListingScreen -> {
                            navController.navigate(
                                Screen.CreateListingWizardScreen.createNavRoute()
                            )
                        }

                        is MyListingsContract.Effect.Navigation.GoToEditListingScreen -> {
                            navController.navigate(
                                Screen.CreateListingWizardScreen.createNavRoute(
                                    navigationEffect.listingId
                                )
                            )
                        }

                        is MyListingsContract.Effect.Navigation.GoToListingDetailsScreen -> {
                            navController.navigate(
                                Screen.ListingDetailsScreen.createNavRoute(
                                    navigationEffect.listingId
                                )
                            )
                        }
                    }
                }
            )
        }

        cartNavigation(
            navController = navController,
            bottomNavigationBarData = { getBottomNavigationBarData(navController) }
        )

        composable(screen = Screen.CheckoutWizardScreen) {
            val viewModel = koinViewModel<CheckoutWizardViewModel>()
            CheckoutWizardScreen(
                onEvent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                onNavigationEffect = { navigationEffect ->
                    when (navigationEffect) {
                        CheckoutWizardContract.Effect.Navigation.ExitWizard -> navController.navigateUp()
                        is CheckoutWizardContract.Effect.Navigation.NavigateToExternalScreen -> {
                            val externalScreenRoute = navigationEffect.externalScreen.route
                            navController.navigate(externalScreenRoute) {
                                popUpTo(externalScreenRoute) {
                                    inclusive = true
                                }
                            }
                        }

                        is CheckoutWizardContract.Effect.Navigation.NavigateToOrderDetailsScreen -> {
                            val route =
                                Screen.OrderDetailsScreen.createNavRoute(navigationEffect.orderId)
                            navController.navigate(route) {
                                popUpTo(route) {
                                    inclusive = true
                                }
                            }
                        }
                    }
                },
                checkoutShippingDetailsViewModel = koinViewModel<CheckoutShippingDetailsViewModel>()
            )
        }

        composable(screen = Screen.OrdersScreen) {
            val viewModel = koinViewModel<OrdersViewModel>()
            OrdersScreen(
                onEvent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                onNavigationEffect = { navigationEffect ->
                    when (navigationEffect) {
                        OrdersContract.Effect.Navigation.GoBack -> navController.navigateUp()
                        is OrdersContract.Effect.Navigation.GoToOrderDetailsScreen -> {
                            navController.navigate(
                                Screen.OrderDetailsScreen.createNavRoute(
                                    navigationEffect.orderId
                                )
                            )
                        }
                    }
                },
            )
        }

        composable(screen = Screen.OrderDetailsScreen) { navBackStackEntry ->
            val viewModel = koinViewModel<OrderDetailsViewModel>()
            OrderDetailsScreen(
                onEvent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                onNavigationEffect = { navigationEffect ->
                    when (navigationEffect) {
                        OrderDetailsContract.Effect.Navigation.GoBack -> navController.navigateUp()
                        is OrderDetailsContract.Effect.Navigation.NavigateToOrderItemTrackingHistoryScreen -> navController.navigate(
                            Screen.OrderItemTrackingHistoryScreen.createNavRoute(
                                orderId = navigationEffect.orderId,
                                orderItemId = navigationEffect.orderItemId,
                                sellerUserId = navigationEffect.sellerUserId,
                                orderItemTitle = navigationEffect.orderItemTitle,
                                orderItemCoverImageUrl = navigationEffect.orderItemCoverImageUrl,
                                orderItemPriceLabel = navigationEffect.orderItemPriceLabel
                            )
                        )
                    }
                },
                orderId = navBackStackEntry.arguments?.getString(NavItem.OrderId.key).orEmpty()
            )
        }

        composable(screen = Screen.OrderItemTrackingHistoryScreen) { navBackStackEntry ->
            val viewModel = koinViewModel<OrderItemTrackingHistoryViewModel>()
            OrderItemTrackingHistoryScreen(
                onEvent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                onNavigationEffect = { navigationEffect ->
                    when (navigationEffect) {
                        OrderItemTrackingHistoryContract.Effect.Navigation.GoBack -> navController.navigateUp()
                        is OrderItemTrackingHistoryContract.Effect.Navigation.GoToProfileDetailsScreen -> navController.navigate(
                            Screen.ProfileDetailsScreen.createNavRoute(navigationEffect.userId)
                        )

                        is OrderItemTrackingHistoryContract.Effect.Navigation.GoToRequestItemReturnScreen -> navController.navigate(
                            Screen.RequestItemReturnScreen.createNavRoute(
                                orderId = navigationEffect.orderId,
                                orderItemId = navigationEffect.orderItemId,
                                orderItemTitle = navigationEffect.orderItemTitle,
                                orderItemCoverImageUrl = navigationEffect.orderItemCoverImageUrl,
                                orderItemPriceLabel = navigationEffect.orderItemPriceLabel
                            )
                        )

                        is OrderItemTrackingHistoryContract.Effect.Navigation.GoToRequestItemRefundScreen -> navController.navigate(
                            Screen.RequestItemRefundScreen.createNavRoute(
                                orderId = navigationEffect.orderId,
                                orderItemId = navigationEffect.orderItemId,
                                orderItemTitle = navigationEffect.orderItemTitle,
                                orderItemCoverImageUrl = navigationEffect.orderItemCoverImageUrl,
                                orderItemPriceLabel = navigationEffect.orderItemPriceLabel
                            )
                        )

                        is OrderItemTrackingHistoryContract.Effect.Navigation.GoToContactSupportScreen -> TODO()
                    }
                },
                orderId = navBackStackEntry.arguments?.getString(NavItem.OrderId.key).orEmpty(),
                orderItemId = navBackStackEntry.arguments?.getString(NavItem.OrderItemId.key)
                    .orEmpty(),
                sellerUserId = navBackStackEntry.arguments?.getString(NavItem.SellerUserId.key)
                    .orEmpty(),
                orderItemTitle = navBackStackEntry.arguments?.getString(NavItem.OrderItemTitle.key)
                    .orEmpty(),
                orderItemCoverImageUrl = navBackStackEntry.arguments?.getString(NavItem.OrderItemCoverImageUrl.key)
                    .orEmpty(),
                orderItemPriceLabel = navBackStackEntry.arguments?.getString(NavItem.OrderItemPriceLabel.key)
                    .orEmpty()
            )
        }

        composable(screen = Screen.RequestItemReturnScreen) { navBackStackEntry ->
            val viewModel = koinViewModel<RequestItemReturnViewModel>()
            RequestItemReturnScreen(
                onEvent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                onNavigationEffect = { navigationEffect ->
                    when (navigationEffect) {
                        RequestItemReturnContract.Effect.Navigation.GoBack -> navController.navigateUp()
                    }
                },
                orderId = navBackStackEntry.arguments?.getString(NavItem.OrderId.key).orEmpty(),
                orderItemId = navBackStackEntry.arguments?.getString(NavItem.OrderItemId.key)
                    .orEmpty(),
                orderItemTitle = navBackStackEntry.arguments?.getString(NavItem.OrderItemTitle.key)
                    .orEmpty(),
                orderItemCoverImageUrl = navBackStackEntry.arguments?.getString(NavItem.OrderItemCoverImageUrl.key)
                    .orEmpty(),
                orderItemPriceLabel = navBackStackEntry.arguments?.getString(NavItem.OrderItemPriceLabel.key)
                    .orEmpty()
            )
        }



        composable(screen = Screen.AllCharactersScreen) {
            val viewModel = koinViewModel<AllCharactersViewModel>()
            AllCharactersScreen(
                onEventSent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                navigateToCharacterScreen = { characterId ->
                    navController.navigate(
                        Screen.CharacterScreen.createNavRoute(
                            characterId = characterId
                        )
                    ) {
                        launchSingleTop = true
                    }
                },
                openDeepLinkAction = {
                    openDeepLinkAction(navController, it)
                },
                bottomNavigationBarData = { getBottomNavigationBarData(navController = navController) }
            )
        }

        composable(
            screen = Screen.CharacterScreen,
            enterTransition = { EnterTransition.RightToLeft },
            exitTransition = { ExitTransition.LeftToRight },
        ) { navBackStackEntry ->
            val viewModel = koinViewModel<CharacterViewModel>()
            CharacterScreen(
                onEventSent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                characterId = navBackStackEntry.arguments?.getInt(NavItem.CharacterId.key)
                    ?: -1,
                popBackStack = navController::navigateUp,
                navigateToComicsScreen = { comicId ->
                    navController.navigate(
                        Screen.ComicScreen.createNavRoute(
                            comicId = comicId
                        )
                    ) {
                        launchSingleTop = true
                    }
                },
                navigateToAllComicsScreen = { characterId, toolbarTitlePrefix ->
                    navController.navigate(
                        Screen.AllCharacterComicsScreen.createNavRoute(
                            characterId = characterId,
                            toolbarTitlePrefix = toolbarTitlePrefix
                        )
                    )
                }
            )
        }

        composable(
            screen = Screen.ComicScreen
        ) { navBackStackEntry ->
            val viewModel = koinViewModel<ComicViewModel>()
            ComicScreen(
                onEventSent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                popBackStack = navController::navigateUp,
                comicId = navBackStackEntry.arguments?.getInt(NavItem.ComicId.key) ?: -1
            )
        }

        composable(
            screen = Screen.AllCharacterComicsScreen
        ) { navBackStackEntry ->
            val viewModel = koinViewModel<AllCharacterComicsViewModel>()
            AllCharacterComicsScreen(
                onEventSent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                popBackStack = navController::navigateUp,
                navigateToComicScreen = { comicId ->
                    navController.navigate(
                        Screen.ComicScreen.createNavRoute(
                            comicId = comicId
                        )
                    ) {
                        launchSingleTop = true
                    }
                },
                characterId = navBackStackEntry.arguments?.getInt(NavItem.CharacterId.key) ?: -1,
                toolbarTitlePrefix = navBackStackEntry.arguments?.getString(NavItem.ToolbarTitlePrefix.key)
                    .orEmpty()
            )
        }

        composable(Screen.AllFavoriteCharactersScreen) {
            val viewModel = koinViewModel<AllFavoriteCharactersViewModel>()
            AllFavoriteCharactersScreen(
                onEventSent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                bottomNavigationBarData = { getBottomNavigationBarData(navController = navController) }
            )
        }

        composable(Screen.FriendsScreen) {
            val viewModel = koinViewModel<FriendsViewModel>()
            FriendsScreen(
                onEventSent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                onNavigationEffect = { navigationEffect ->
                    when (navigationEffect) {
                        FriendsContract.Effect.Navigation.GoBack -> {
                            navController.navigateUp()
                        }

                        is FriendsContract.Effect.Navigation.NavigateToProfileDetailsScreen -> {
                            navController.navigate(
                                Screen.ProfileDetailsScreen.createNavRoute(userId = navigationEffect.userId)
                            )
                        }
                    }
                }
            )
        }

        composable(Screen.ProfileScreen) {
            val viewModel = koinViewModel<ProfileViewModel>()
            ProfileScreen(
                onEventSent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
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
                openDeepLinkAction = { deepLinkAction ->
                    openDeepLinkAction(
                        navController = navController,
                        deepLinkAction = deepLinkAction
                    )
                }
            )
        }

        composable(
            screen = Screen.ProfileImageScreen,
            enterTransition = { EnterTransition.BottomToTop },
            exitTransition = { ExitTransition.TopToBottom }
        ) { navBackStackEntry ->
            val viewModel = koinViewModel<ProfileImageViewModel>()
            ProfileImageScreen(
                onEventSent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                navigateUp = navController::navigateUp,
                profileImageUrl = navBackStackEntry.arguments?.getString(NavItem.ProfileImageUrl.key)
                    .orEmpty()
            )
        }

        composable(
            screen = Screen.ProfileDetailsScreen,
            enterTransition = { EnterTransition.RightToLeft },
            exitTransition = { ExitTransition.LeftToRight },
        ) { navBackStackEntry ->
            val viewModel = koinViewModel<ProfileDetailsViewModel>()
            ProfileDetailsScreen(
                onEventSent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                userId = navBackStackEntry.arguments?.getString(NavItem.UserId.key).orEmpty(),
                shouldShowAcceptFriendRequestDialog = navBackStackEntry.arguments?.getBoolean(
                    NavItem.ShouldShowAcceptFriendRequestDialog.key
                ) ?: false,
                onNavigation = { navigationEffect ->
                    when (navigationEffect) {
                        ProfileDetailsContract.Effect.Navigation.PopBackStack -> {
                            navController.navigateUp()
                        }

                        is ProfileDetailsContract.Effect.Navigation.NavigateToProfileImageScreen -> {
                            navController.navigate(
                                Screen.ProfileImageScreen.createNavRoute(
                                    navigationEffect.profileImageUrl
                                )
                            )
                        }

                        ProfileDetailsContract.Effect.Navigation.NavigateToEditProfileDetailsScreen -> {
                            navController.navigate(Screen.EditProfileDetailsScreen.route)
                        }

                        is ProfileDetailsContract.Effect.Navigation.NavigateToProfileDetailsStatsScreen -> {
                            navController.navigate(
                                Screen.ProfileDetailsStatsScreen.createNavRoute(
                                    fullName = navigationEffect.fullName,
                                    userId = navigationEffect.userId,
                                    tabIndex = navigationEffect.tab.tabIndex
                                )
                            )
                        }
                    }
                },
            )
        }

        composable(Screen.ProfileDetailsStatsScreen) { navBackStackEntry ->
            val viewModel = koinViewModel<ProfileDetailsStatsViewModel>()
            ProfileDetailsStatsScreen(
                onEventSent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                fullName = navBackStackEntry.arguments?.getString(NavItem.FullName.key).orEmpty(),
                userId = navBackStackEntry.arguments?.getString(NavItem.UserId.key).orEmpty(),
                tabIndex = navBackStackEntry.arguments?.getInt(NavItem.TabIndex.key) ?: 0,
                onNavigationEffect = { effect ->
                    when (effect) {
                        ProfileDetailsStatsContract.Effect.Navigation.NavigateUp -> navController.navigateUp()
                        is ProfileDetailsStatsContract.Effect.Navigation.NavigateToProfileDetailsScreen -> navController.navigate(
                            Screen.ProfileDetailsScreen.createNavRoute(userId = effect.userId)
                        )

                        is ProfileDetailsStatsContract.Effect.Navigation.NavigateToListingScreen -> TODO(
                            "Not implemented yet."
                        )

                        is ProfileDetailsStatsContract.Effect.Navigation.NavigateToCharacterScreen -> navController.navigate(
                            Screen.CharacterScreen.createNavRoute(characterId = effect.characterId)
                        )
                    }
                }
            )
        }

        composable(
            screen = Screen.EditProfileDetailsScreen,
            enterTransition = { EnterTransition.RightToLeft },
            exitTransition = { ExitTransition.LeftToRight },
        ) {
            val viewModel = koinViewModel<EditProfileDetailsViewModel>()
            EditProfileDetailsScreen(
                onEventSent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                navigateUp = navController::navigateUp,
            )
        }

        composable(
            screen = Screen.SettingsScreen
        ) {
            val viewModel = koinViewModel<SettingsViewModel>()

            SettingsScreen(
                onEvent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                onNavigationEffect = {
                    when (it) {
                        SettingsContract.Effect.Navigation.GoBack -> navController.navigateUp()
                        SettingsContract.Effect.Navigation.NavigateToFaqScreen -> navController.navigate(
                            Screen.FaqScreen.route
                        )

                        SettingsContract.Effect.Navigation.NavigateToHomeScreen -> {
                            navController.navigate(Screen.HomeScreen.route) {
                                popUpToTop(navController)
                            }
                        }

                        SettingsContract.Effect.Navigation.NavigateToPrivacySettingsScreen -> navController.navigate(
                            Screen.PrivacySettingsScreen.route
                        )
                    }
                }
            )
        }

        composable(
            screen = Screen.PrivacySettingsScreen,
            enterTransition = { EnterTransition.RightToLeft },
            exitTransition = { ExitTransition.LeftToRight },
        ) {
            val viewModel = koinViewModel<PrivacySettingsViewModel>()
            PrivacySettingsScreen(
                onEventSent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                popBackStack = navController::navigateUp,
            )
        }

        composable(
            screen = Screen.FaqScreen,
            enterTransition = { EnterTransition.RightToLeft },
            exitTransition = { ExitTransition.LeftToRight },
        ) {
            val viewModel = koinViewModel<FaqViewModel>()
            FaqScreen(
                onEventSent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                navigateUp = navController::navigateUp,
            )
        }

        composable(Screen.QrCodeScreen) { navBackStackEntry ->
            val viewModel = koinViewModel<QrCodeScanViewModel>()
            QrCodeScanScreen(
                onEventSent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                previousScreenRoute = navBackStackEntry.arguments?.getString(NavItem.PreviousScreenRoute.key)
                    .orEmpty(),
                onNavigation = {
                    when (it) {
                        QrCodeScanContract.Effect.Navigation.NavigateUp -> navController.navigateUp()

                        QrCodeScanContract.Effect.Navigation.OpenAppSettings -> {
                            navController.context.openAppSettings()
                        }
                    }
                },
                navigateUpAndOpenDeepLinkAction = { deepLinkAction ->
                    navController.navigateUp()
                    openDeepLinkAction(
                        navController = navController,
                        deepLinkAction = deepLinkAction
                    )
                },
                navigateUp = navController::navigateUp
            )
        }
    }
}

/**
 * Returns the [BottomNavigationBarData] required for the bottom navigation bar in the main screens.
 *
 * @param navController The associated [NavController] of the graph.
 */
@Composable
fun getBottomNavigationBarData(navController: NavController): BottomNavigationBarData =
    BottomNavigationBarData(
        navController = navController,
        items = getBottomNavigationItems(),
        backgroundColor = MaterialTheme.colors.background,
        topHorizontalColor = MaterialTheme.colors.onBackground.copy(alpha = 0.8f),
        selectedItemTopHorizontalColor = MaterialTheme.customColors.genericRed.copy(alpha = 0.8f),
        elevation = SPACING_MEDIUM.dp
    )

/**
 * Returns the bottom navigation items.
 */
private fun getBottomNavigationItems(): List<BottomBarItem> = listOf(
    BottomBarItem(
        route = Screen.MarketplaceScreen.route,
        label = R.string.bottom_navigation_label_marketplace,
        iconData = IconData(
            iconResource = R.drawable.ic_marketplace,
            contentDescription = R.string.bottom_navigation_label_marketplace
        )
    ),
    BottomBarItem(
        route = Screen.AllCharactersScreen.route,
        label = R.string.bottom_navigation_label_characters,
        iconData = IconData(
            iconResource = R.drawable.ic_superhero,
            contentDescription = R.string.bottom_navigation_label_characters
        )
    ),
    BottomBarItem(
        route = Screen.AllFavoriteCharactersScreen.route,
        label = R.string.bottom_navigation_label_favorites,
        iconData = IconData(
            iconResource = R.drawable.ic_favorite_filled,
            contentDescription = R.string.bottom_navigation_label_favorites
        )
    ),
    BottomBarItem(
        route = Screen.CartScreen.route,
        label = R.string.bottom_navigation_label_cart,
        iconData = IconData(
            iconResource = R.drawable.ic_cart,
            contentDescription = R.string.bottom_navigation_label_cart
        )
    ),
    BottomBarItem(
        route = Screen.ProfileScreen.route,
        label = R.string.bottom_navigation_label_profile,
        iconData = IconData(
            iconResource = R.drawable.ic_person,
            contentDescription = R.string.bottom_navigation_label_profile
        )
    )
)
