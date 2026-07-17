package com.alexgreece45.marvelx.ui.navigation

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.components.BottomBarItem
import com.alexgreece45.marvelx.ui.components.BottomNavigationBarData
import com.alexgreece45.marvelx.ui.components.IconData
import com.alexgreece45.marvelx.ui.helper.openDeepLinkAction
import com.alexgreece45.marvelx.ui.screens.allCharacters.navigation.AllCharactersRoute
import com.alexgreece45.marvelx.ui.screens.allCharacters.navigation.allCharactersScreen
import com.alexgreece45.marvelx.ui.screens.allCharacters.navigation.navigateToAllCharactersScreen
import com.alexgreece45.marvelx.ui.screens.allFavoriteCharacters.navigation.AllFavoriteCharactersRoute
import com.alexgreece45.marvelx.ui.screens.allFavoriteCharacters.navigation.allFavoriteCharactersScreen
import com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.CheckoutWizardScreen
import com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.CheckoutWizardViewModel
import com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.model.CheckoutWizardContract
import com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.steps.shippingDetails.CheckoutShippingDetailsViewModel
import com.alexgreece45.marvelx.ui.screens.cart.navigation.CartRoute
import com.alexgreece45.marvelx.ui.screens.cart.navigation.cartScreen
import com.alexgreece45.marvelx.ui.screens.changePassword.ChangePasswordScreen
import com.alexgreece45.marvelx.ui.screens.character.allCharacterComicsScreen.navigation.allCharacterComicsScreen
import com.alexgreece45.marvelx.ui.screens.character.allCharacterComicsScreen.navigation.navigateToAllCharacterComicsScreen
import com.alexgreece45.marvelx.ui.screens.character.navigation.characterScreen
import com.alexgreece45.marvelx.ui.screens.character.navigation.navigateToCharacterScreen
import com.alexgreece45.marvelx.ui.screens.comic.navigation.comicScreen
import com.alexgreece45.marvelx.ui.screens.comic.navigation.navigateToComicScreen
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
import com.alexgreece45.marvelx.ui.screens.login.LoginEffect
import com.alexgreece45.marvelx.ui.screens.login.LoginScreen
import com.alexgreece45.marvelx.ui.screens.login.LoginViewModel
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.navigation.createListingWizardScreen
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.navigation.navigateToCreateListingWizardScreen
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.success.navigation.createListingSuccessScreen
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.success.navigation.navigateToCreateListingSuccessScreen
import com.alexgreece45.marvelx.ui.screens.marketplace.listingDetailsScreen.navigation.listingDetailsScreen
import com.alexgreece45.marvelx.ui.screens.marketplace.listingDetailsScreen.navigation.navigateToListingDetailsScreen
import com.alexgreece45.marvelx.ui.screens.marketplace.navigation.MarketplaceRoute
import com.alexgreece45.marvelx.ui.screens.marketplace.navigation.marketplaceScreen
import com.alexgreece45.marvelx.ui.screens.marketplace.navigation.navigateToMarketplaceScreen
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
import com.alexgreece45.marvelx.ui.screens.otp.OtpEffect
import com.alexgreece45.marvelx.ui.screens.otp.OtpScreen
import com.alexgreece45.marvelx.ui.screens.otp.OtpViewModel
import com.alexgreece45.marvelx.ui.screens.profile.editProfileDetails.EditProfileDetailsContract
import com.alexgreece45.marvelx.ui.screens.profile.editProfileDetails.EditProfileDetailsScreen
import com.alexgreece45.marvelx.ui.screens.profile.editProfileDetails.EditProfileDetailsViewModel
import com.alexgreece45.marvelx.ui.screens.profile.image.ProfileImageScreen
import com.alexgreece45.marvelx.ui.screens.profile.image.ProfileImageViewModel
import com.alexgreece45.marvelx.ui.screens.profile.navigation.ProfileRoute
import com.alexgreece45.marvelx.ui.screens.profile.navigation.profileScreen
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
import com.alexgreece45.marvelx.ui.screens.signup.SignupEffect
import com.alexgreece45.marvelx.ui.screens.signup.SignupViewModel
import com.alexgreece45.marvelx.ui.screens.splash.SplashScreen
import com.alexgreece45.marvelx.ui.transitions.EnterTransition
import com.alexgreece45.marvelx.ui.transitions.ExitTransition
import com.alexgreece45.marvelx.ui_theme.theme.values.customColors
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
internal fun NavGraph(navController: NavHostController) {
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
                            navController.navigateToAllCharactersScreen(clearBackStack = true)
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
                onNavigationEffect = { navigationEffect ->
                    when (navigationEffect) {
                        is LoginEffect.Navigation.NavigateToOtpScreen -> {
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

                        LoginEffect.Navigation.NavigateToSignupScreen -> {
                            navController.navigate(Screen.SignupScreen.route) {
                                launchSingleTop = true
                            }
                        }

                        is LoginEffect.Navigation.PopBackStack -> {
                            navController.navigateUp()
                        }

                        LoginEffect.Navigation.NavigateToForgotPasswordScreen -> {
                            navController.navigate(Screen.ForgotPasswordScreen.route)
                        }

                        is LoginEffect.Navigation.NavigateToAllCharactersScreen -> {
                            navController.navigateToAllCharactersScreen(clearBackStack = true)
                        }
                    }
                }
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
                onEvent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                onNavigationEffect = { navigationEffect ->
                    when (navigationEffect) {
                        SignupEffect.Navigation.GoBack -> {
                            navController.navigateUp()
                        }

                        SignupEffect.Navigation.NavigateToAllCharactersScreen -> {
                            navController.navigateToAllCharactersScreen(true)
                        }

                        is SignupEffect.Navigation.NavigateToOtpScreen -> {
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
                    }
                }
            )
        }

        composable(Screen.OtpScreen) { navBackStackEntry ->
            val viewModel = koinViewModel<OtpViewModel>()
            OtpScreen(
                onEventSent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                onNavigationEffect = { navigationEffect ->
                    when (navigationEffect) {
                        OtpEffect.Navigation.GoBack -> {
                            navController.navigateUp()
                        }

                        OtpEffect.Navigation.NavigateToAllCharactersScreen -> {
                            navController.navigateToAllCharactersScreen(true)
                        }

                        is OtpEffect.Navigation.NavigateToChangePasswordScreen -> {
                            navController.navigate(
                                Screen.ChangePasswordScreen.createNavRoute(
                                    requestIdEmail = navigationEffect.requestIdEmail,
                                    requestIdPhoneNumber = navigationEffect.requestIdPhoneNumber
                                )
                            )
                        }
                    }
                },
                userId = navBackStackEntry.arguments?.getString(NavItem.UserId.key),
                otpFlowType = navBackStackEntry.arguments?.getString(NavItem.OtpFlowType.key),
                verificationStep = navBackStackEntry.arguments?.getString(NavItem.VerificationStep.key),
                shouldRememberUserSession =
                    navBackStackEntry.arguments?.getBoolean(NavItem.ShouldRememberUserSession.key),
                requestIdEmail = navBackStackEntry.arguments?.getString(NavItem.RequestIdEmail.key),
                requestIdPhoneNumber =
                    navBackStackEntry.arguments?.getString(NavItem.RequestIdPhoneNumber.key)
            )
        }

        // Main screens.

        // TODO: Resolve later.
//        mainGraph(
//            navController = navController,
//            bottomNavigationBarData = { getBottomNavigationBarData(navController) }
//        )

        marketplaceScreen(
            navigateToListingDetailsScreen = navController::navigateToListingDetailsScreen,
            bottomNavigationBarData = { getBottomNavigationBarData(navController) }
        )

        listingDetailsScreen(
            goBack = navController::navigateUp,
            navigateToProfileDetailsScreen = { userId ->
                navController.navigate(
                    Screen.ProfileDetailsScreen.createNavRoute(
                        userId = userId
                    )
                )
            },
            navigateToEditListingScreen = navController::navigateToCreateListingWizardScreen
        )

        createListingWizardScreen(
            goBack = navController::navigateUp,
            navigateToListingDetailsScreen = navController::navigateToListingDetailsScreen,
            navigateToCreateListingSuccessScreen = navController::navigateToCreateListingSuccessScreen
        )

        createListingSuccessScreen(
            navigateToListingDetailsScreen = navController::navigateToListingDetailsScreen,
            exitWizard = navController::navigateUp
        )

        characterScreen(
            goBack = navController::navigateUp,
            navigateToComicScreen = navController::navigateToComicScreen,
            navigateToAllCharacterComicsScreen = navController::navigateToAllCharacterComicsScreen,
        )

        composable(
            screen = Screen.MyListingsScreen,
            enterTransition = { EnterTransition.RightToLeft },
            exitTransition = { ExitTransition.LeftToRight },
        ) {
            val viewModel = koinViewModel<MyListingsViewModel>()
            MyListingsScreen(
                onEvent = viewModel::setEvent,
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                effect = viewModel.uiEffect,
                onNavigationEffect = { navigationEffect ->
                    when (navigationEffect) {
                        MyListingsContract.Effect.Navigation.GoBack -> navController.navigateUp()
                        MyListingsContract.Effect.Navigation.GoToCreateListingScreen -> {
                            navController.navigateToCreateListingWizardScreen(null)
                        }

                        is MyListingsContract.Effect.Navigation.GoToEditListingScreen -> {
                            navController.navigateToCreateListingWizardScreen(navigationEffect.listingId)
                        }

                        is MyListingsContract.Effect.Navigation.GoToListingDetailsScreen -> {
                            navController.navigateToListingDetailsScreen(navigationEffect.listingId)
                        }
                    }
                }
            )
        }

        cartScreen(
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
                        CheckoutWizardContract.Effect.Navigation.NavigateToMarketplaceScreen -> {
                            navController.navigateToMarketplaceScreen(clearBackStack = true)
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

        composable(
            screen = Screen.OrdersScreen,
            enterTransition = { EnterTransition.RightToLeft },
            exitTransition = { ExitTransition.LeftToRight },
        ) {
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
                            Screen.RequestItemRefundScreen.createNavRoute(orderItemDetails = navigationEffect.orderItemDetails)
                        )

                        is OrderItemTrackingHistoryContract.Effect.Navigation.GoToContactSupportScreen -> TODO()
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
                    .orEmpty(),
                sellerUserId = navBackStackEntry.arguments?.getString(NavItem.SellerUserId.key)
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

        allCharactersScreen(
            navController = navController,
            navigateToCharacterScreen = { characterId ->
                navController.navigateToCharacterScreen(
                    characterId = characterId,
                    navOptionsBuilder = {
                        navOptions {
                            launchSingleTop = true
                        }
                    }
                )
            },
            openDeepLinkAction = navController::openDeepLinkAction
        )

        comicScreen(goBack = navController::navigateUp)

        allCharacterComicsScreen(
            goBack = navController::navigateUp,
            navigateToComicScreen = { comicId ->
                navController.navigateToComicScreen(comicId)
            }
        )

        allFavoriteCharactersScreen(navController)

        composable(
            screen = Screen.FriendsScreen,
            enterTransition = { EnterTransition.RightToLeft },
            exitTransition = { ExitTransition.LeftToRight },
        ) {
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

        profileScreen(navController)

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
                onNavigationEffect = { navigationEffect ->
                    when (navigationEffect) {
                        EditProfileDetailsContract.Effect.Navigation.NavigateUp -> {
                            navController.navigateUp()
                        }

                        is EditProfileDetailsContract.Effect.Navigation.NavigateUpWithSnackbarPayload -> {
                            navController.previousBackStackEntry?.savedStateHandle["snackbarNavigationPayload"] =
                                navigationEffect.snackbarNavigationPayload
                            navController.navigateUp()
                        }
                    }
                }
            )
        }

        composable(
            screen = Screen.SettingsScreen,
            enterTransition = { EnterTransition.RightToLeft },
            exitTransition = { ExitTransition.LeftToRight },
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
                    navController.openDeepLinkAction(deepLinkAction)
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
        rootItem = rootBottomBarItem,
        items = getBottomNavigationItems(),
        backgroundColor = MaterialTheme.colors.background,
        topHorizontalColor = MaterialTheme.colors.onBackground.copy(alpha = 0.8f),
        selectedItemTopHorizontalColor = MaterialTheme.customColors.genericRed.copy(alpha = 0.8f),
        elevation = SPACING_MEDIUM.dp
    )

private val rootBottomBarItem = BottomBarItem(
    route = AllCharactersRoute,
    label = R.string.bottom_navigation_label_characters,
    iconData = IconData(
        iconResource = R.drawable.ic_superhero,
        contentDescription = R.string.bottom_navigation_label_characters
    )
)

/**
 * Returns the bottom navigation items.
 */
private fun getBottomNavigationItems(): List<BottomBarItem> = listOf(
    BottomBarItem(
        route = MarketplaceRoute,
        label = R.string.bottom_navigation_label_marketplace,
        iconData = IconData(
            iconResource = R.drawable.ic_marketplace,
            contentDescription = R.string.bottom_navigation_label_marketplace
        )
    ),
    BottomBarItem(
        route = AllCharactersRoute,
        label = R.string.bottom_navigation_label_characters,
        iconData = IconData(
            iconResource = R.drawable.ic_superhero,
            contentDescription = R.string.bottom_navigation_label_characters
        )
    ),
    BottomBarItem(
        route = AllFavoriteCharactersRoute,
        label = R.string.bottom_navigation_label_favorites,
        iconData = IconData(
            iconResource = R.drawable.ic_favorite_filled,
            contentDescription = R.string.bottom_navigation_label_favorites
        )
    ),
    BottomBarItem(
        route = CartRoute,
        label = R.string.bottom_navigation_label_cart,
        iconData = IconData(
            iconResource = R.drawable.ic_cart,
            contentDescription = R.string.bottom_navigation_label_cart
        )
    ),
    BottomBarItem(
        route = ProfileRoute(),
        label = R.string.bottom_navigation_label_profile,
        iconData = IconData(
            iconResource = R.drawable.ic_person,
            contentDescription = R.string.bottom_navigation_label_profile
        )
    )
)