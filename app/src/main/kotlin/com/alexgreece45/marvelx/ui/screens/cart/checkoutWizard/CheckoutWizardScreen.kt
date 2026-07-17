package com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.domain.model_api.order.OrderCheckoutInfoDomain
import com.alexgreece45.marvelx.ui.components.AlertDialogButton
import com.alexgreece45.marvelx.ui.components.AlertDialogError
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.WrapButton
import com.alexgreece45.marvelx.ui.components.effect.ObserveUiEffect
import com.alexgreece45.marvelx.ui.components.effect.OneTimeLaunchedEffect
import com.alexgreece45.marvelx.ui.components.model.ErrorButtonType
import com.alexgreece45.marvelx.ui.screens.cart.CartScreen
import com.alexgreece45.marvelx.ui.screens.cart.CartViewModel
import com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.model.CheckoutWizardContract
import com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.model.CheckoutWizardContract.Event.OnNavigationEvents.EditPaymentDetailsScreen
import com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.model.CheckoutWizardContract.Event.OnNavigationEvents.EditShippingDetailsScreen
import com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.model.CheckoutWizardContract.Event.OnNavigationEvents.OnNavigateToMarketplaceScreen
import com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.model.CheckoutWizardContract.Event.OnNavigationEvents.OrderDetailsScreen
import com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.model.CheckoutWizardContract.Event.OnNavigationEvents.OrderSuccessScreen
import com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.model.CheckoutWizardStep
import com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.steps.paymentDetails.CheckoutPaymentDetailsScreen
import com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.steps.paymentDetails.CheckoutPaymentDetailsViewModel
import com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.steps.paymentDetails.model.CheckoutPaymentDetailsContract
import com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.steps.shippingDetails.CheckoutShippingDetailsScreen
import com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.steps.shippingDetails.CheckoutShippingDetailsViewModel
import com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.steps.shippingDetails.model.CheckoutShippingDetailsContract
import com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.steps.success.CheckoutSuccessScreen
import com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.steps.success.CheckoutSuccessViewModel
import com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.steps.success.model.CheckoutSuccessContract
import com.alexgreece45.marvelx.ui.screens.cart.model.CartContract
import com.alexgreece45.marvelx.ui.screens.orders.model.CardBrandUi
import com.alexgreece45.marvelx.ui.screens.orders.model.toData
import com.alexgreece45.marvelx.ui_theme.theme.values.customColors
import com.alexgreece45.marvelx.util.DEFAULT_SCREEN_PADDING
import com.alexgreece45.marvelx.util.extensions.showToast
import kotlinx.coroutines.flow.Flow
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun CheckoutWizardScreen(
    onEvent: (CheckoutWizardContract.Event) -> Unit,
    state: CheckoutWizardContract.State,
    effect: Flow<CheckoutWizardContract.Effect>,
    onNavigationEffect: (CheckoutWizardContract.Effect.Navigation) -> Unit,
    checkoutShippingDetailsViewModel: CheckoutShippingDetailsViewModel = koinViewModel(),
    checkoutPaymentDetailsViewModel: CheckoutPaymentDetailsViewModel = koinViewModel()
) {

    val checkoutShippingDetailsState =
        checkoutShippingDetailsViewModel.uiState.collectAsStateWithLifecycle().value
    val checkoutPaymentDetailsState =
        checkoutPaymentDetailsViewModel.uiState.collectAsStateWithLifecycle().value

    val context = LocalContext.current

    ObserveUiEffect(effect) { uiEffect ->
        when (uiEffect) {
            is CheckoutWizardContract.Effect.Navigation -> onNavigationEffect(uiEffect)
            is CheckoutWizardContract.Effect.ShowToastMessage -> {
                context.showToast(uiEffect.message)
            }
        }
    }

    OneTimeLaunchedEffect {
        onEvent(CheckoutWizardContract.Event.Init)
    }

    ContentScreen(
        isLoading = false,
        navigableAction = when (state.wizardStep) {
            CheckoutWizardStep.SHIPPING_DETAILS,
            CheckoutWizardStep.SUCCESS -> {
                ScreenNavigableAction.Closeable(
                    onClose = {
                        onEvent(CheckoutWizardContract.Event.OnCloseButtonClicked)
                    }
                )
            }

            CheckoutWizardStep.PAYMENT_DETAILS,
            CheckoutWizardStep.OVERVIEW -> {
                ScreenNavigableAction.Backable(
                    onBack = {
                        onEvent(CheckoutWizardContract.Event.OnBackButtonClicked)
                    }
                )
            }
        },
        onBack = {
            onEvent(CheckoutWizardContract.Event.OnBackButtonClicked)
        },
        showToolbar = true,
        toolbarTitle = state.toolbarTitle,
        stickyBottom = if (state.wizardStep == CheckoutWizardStep.SUCCESS
            || state.wizardStep == CheckoutWizardStep.OVERVIEW
        ) null else {
            {
                WrapButton(
                    onClick = { onEvent(CheckoutWizardContract.Event.OnStickyBottomButtonClicked) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(DEFAULT_SCREEN_PADDING),
                    enabled = state.stickyBottomButtonEnabled
                ) {
                    Text(text = stringResource(R.string.screen_checkout_wizard_sticky_bottom_button_next))
                }
            }
        },
        paddingValues = PaddingValues(0.dp)
    ) { paddingValues ->
        val layoutDirection = LocalLayoutDirection.current
        val verticalPaddings = PaddingValues(
            top = paddingValues.calculateTopPadding(),
            bottom = paddingValues.calculateBottomPadding()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(verticalPaddings)
        ) {
            AnimatedVisibility(visible = state.wizardStep != CheckoutWizardStep.SUCCESS) {
                LinearProgressIndicator(
                    progress = state.wizardProgress,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.customColors.genericRed
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        PaddingValues(
                            start = paddingValues.calculateStartPadding(layoutDirection),
                            end = paddingValues.calculateEndPadding(layoutDirection)
                        )
                    )
            ) {
                when (state.wizardStep) {
                    CheckoutWizardStep.SHIPPING_DETAILS -> {
                        CheckoutShippingDetailsScreen(
                            onEvent = checkoutShippingDetailsViewModel::setEvent,
                            state = checkoutShippingDetailsState,
                            effect = checkoutShippingDetailsViewModel.uiEffect,
                            onNavigationEffect = { navigationEffect ->
                                when (navigationEffect) {
                                    CheckoutShippingDetailsContract.Effect.Navigation.ExitWizard -> {
                                        onEvent(CheckoutWizardContract.Event.OnExitWizard)
                                    }
                                }
                            },
                            setStickyBottomButtonEnabled = {
                                onEvent(
                                    CheckoutWizardContract.Event.SetStickyBottomButtonEnabled(
                                        it
                                    )
                                )
                            },
                            showToastMessage = {
                                onEvent(CheckoutWizardContract.Event.OnShowToastMessage(it))
                            }
                        )
                    }

                    CheckoutWizardStep.PAYMENT_DETAILS -> {
                        CheckoutPaymentDetailsScreen(
                            onEvent = checkoutPaymentDetailsViewModel::setEvent,
                            state = checkoutPaymentDetailsState,
                            effect = checkoutPaymentDetailsViewModel.uiEffect,
                            onNavigationEffect = { navigationEffect ->
                                when (navigationEffect) {
                                    CheckoutPaymentDetailsContract.Effect.Navigation.ExitWizard -> {
                                        onEvent(CheckoutWizardContract.Event.OnExitWizard)
                                    }
                                }
                            },
                            setStickyBottomButtonEnabled = {
                                onEvent(
                                    CheckoutWizardContract.Event.SetStickyBottomButtonEnabled(
                                        it
                                    )
                                )
                            },
                            showToastMessage = {
                                onEvent(CheckoutWizardContract.Event.OnShowToastMessage(it))
                            }
                        )
                    }

                    CheckoutWizardStep.OVERVIEW -> {
                        val cartViewModel = koinViewModel<CartViewModel>()
                        val cardNumber = checkoutPaymentDetailsState.cardNumber.value

                        CartScreen(
                            onEvent = cartViewModel::setEvent,
                            state = cartViewModel.uiState.collectAsStateWithLifecycle().value,
                            effect = cartViewModel.uiEffect,
                            onNavigationEffect = { navigationEffect ->
                                when (navigationEffect) {
                                    CartContract.Effect.Navigation.GoBack,
                                    CartContract.Effect.Navigation.GoToCheckoutScreen,
                                    is CartContract.Effect.Navigation.GoToListingDetailsScreen -> {
                                        onEvent(CheckoutWizardContract.Event.OnActionNotSupported)
                                    }

                                    CartContract.Effect.Navigation.GoToMarketplaceScreen -> {
                                        onEvent(OnNavigateToMarketplaceScreen)
                                    }

                                    CartContract.Effect.Navigation.GoToEditCheckoutShippingDetailsScreen -> {
                                        onEvent(EditShippingDetailsScreen)
                                    }

                                    CartContract.Effect.Navigation.GoToEditCheckoutPaymentDetailsScreen -> {
                                        onEvent(EditPaymentDetailsScreen)
                                    }

                                    is CartContract.Effect.Navigation.GoToCheckoutSuccessScreen -> {
                                        onEvent(
                                            OrderSuccessScreen(
                                                navigationEffect.orderId
                                            )
                                        )
                                    }
                                }
                            },
                            bottomNavigationBarData = null,
                            orderCheckoutInfo = OrderCheckoutInfoDomain(
                                firstName = checkoutShippingDetailsState.firstName.value,
                                lastName = checkoutShippingDetailsState.lastName.value,
                                streetAddress = checkoutShippingDetailsState.streetAddress.value,
                                city = checkoutShippingDetailsState.city.value,
                                zipCode = checkoutShippingDetailsState.zipCode.value,
                                maskedCardNumber = cardNumber.takeLast(4),
                                cardBrand = CardBrandUi.getCardBrandUi(cardNumber).toData(),
                                paymentDate = null,
                                transactionId = null
                            )
                        )
                    }

                    CheckoutWizardStep.SUCCESS -> {
                        val checkoutSuccessViewModel = koinViewModel<CheckoutSuccessViewModel>()
                        CheckoutSuccessScreen(
                            onEvent = checkoutSuccessViewModel::setEvent,
                            state = checkoutSuccessViewModel.uiState.collectAsStateWithLifecycle().value,
                            effect = checkoutSuccessViewModel.uiEffect,
                            onNavigationEffect = { navigationEffect ->
                                when (navigationEffect) {
                                    is CheckoutSuccessContract.Effect.Navigation.NavigateToOrderDetailsScreen -> {
                                        onEvent(
                                            OrderDetailsScreen(
                                                navigationEffect.orderId
                                            )
                                        )
                                    }

                                    CheckoutSuccessContract.Effect.Navigation.ExitWizard -> {
                                        onEvent(
                                            CheckoutWizardContract.Event.OnExitWizard
                                        )
                                    }
                                }
                            },
                            orderId = state.orderId
                        )
                    }
                }
            }

            AlertDialogError(
                isVisible = state.alertDialogAction != null,
                title = stringResource(R.string.screen_checkout_wizard_alert_dialog_title),
                errorMessage = {
                    Text(text = stringResource(R.string.screen_checkout_wizard_alert_dialog_message))
                },
                confirmButton = AlertDialogButton(
                    label = stringResource(R.string.screen_checkout_wizard_alert_dialog_button_positive),
                    buttonType = ErrorButtonType.Contained,
                    onClick = { onEvent(CheckoutWizardContract.Event.AlertDialogEvents.OnPositiveButtonClicked) }
                ),
                dismissButton = AlertDialogButton(
                    label = stringResource(R.string.screen_checkout_wizard_alert_dialog_button_negative),
                    onClick = { onEvent(CheckoutWizardContract.Event.AlertDialogEvents.OnNegativeButtonClicked) }
                )
            )
        }
    }
}