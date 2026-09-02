package com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard

import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.model.CheckoutWizardContract
import com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.model.CheckoutWizardExitAlertDialogAction
import com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.model.CheckoutWizardStep
import com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.model.CheckoutWizardStepDirection
import com.alexgreece45.marvelx.util.ResourceProvider
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
internal class CheckoutWizardViewModel(
    private val resourceProvider: ResourceProvider
) : BaseViewModel<CheckoutWizardContract.Event, CheckoutWizardContract.State, CheckoutWizardContract.Effect>() {

    override fun setInitialState(): CheckoutWizardContract.State = CheckoutWizardContract.State()

    override fun handleEvents(event: CheckoutWizardContract.Event) {
        when (event) {
            CheckoutWizardContract.Event.Init -> init()
            CheckoutWizardContract.Event.OnBackButtonClicked -> onBackButtonClicked()
            CheckoutWizardContract.Event.OnCloseButtonClicked -> onCloseButtonClicked()
            CheckoutWizardContract.Event.OnStickyBottomButtonClicked -> onStickyBottomButtonClicked()
            CheckoutWizardContract.Event.AlertDialogEvents.OnNegativeButtonClicked -> onAlertDialogNegativeButtonClicked()
            CheckoutWizardContract.Event.AlertDialogEvents.OnPositiveButtonClicked -> onAlertDialogPositiveButtonClicked()
            is CheckoutWizardContract.Event.SetStickyBottomButtonEnabled -> setStickyBottomButtonEnabled(
                event.enabled
            )

            is CheckoutWizardContract.Event.OnShowToastMessage -> onShowToastMessage(event.message)
            CheckoutWizardContract.Event.OnActionNotSupported -> onActionNotSupported()
            CheckoutWizardContract.Event.OnNavigationEvents.EditShippingDetailsScreen -> onNavigateToEditShippingDetailsScreen()
            CheckoutWizardContract.Event.OnNavigationEvents.EditPaymentDetailsScreen -> onNavigateToEditPaymentDetailsScreen()
            is CheckoutWizardContract.Event.OnNavigationEvents.OrderSuccessScreen -> onNavigateToCheckoutSuccessScreen(
                event.orderId
            )

            is CheckoutWizardContract.Event.OnNavigationEvents.OrderDetailsScreen -> onNavigateToOrderDetailsScreen(
                event.orderId
            )

            CheckoutWizardContract.Event.OnNavigationEvents.OnNavigateToMarketplaceScreen -> onNavigateToMarketplaceScreen()

            CheckoutWizardContract.Event.OnExitWizard -> onExitWizard()
        }
    }

    /**
     * Event functions.
     */

    /**
     * Initializes wizard.
     */
    private fun init() {
        val wizardStep = CheckoutWizardStep.SHIPPING_DETAILS
        setState {
            copy(
                wizardStep = wizardStep,
                toolbarTitle = getToolbarTitle(wizardStep)
            )
        }
    }

    private fun onBackButtonClicked() {
        navigateToStep(CheckoutWizardStepDirection.PREVIOUS)
    }

    private fun onCloseButtonClicked() {
        when (currentState.wizardStep) {
            CheckoutWizardStep.SHIPPING_DETAILS -> {
                setAlertDialogAction(CheckoutWizardExitAlertDialogAction.EXIT_WIZARD)
            }

            CheckoutWizardStep.PAYMENT_DETAILS,
            CheckoutWizardStep.OVERVIEW -> {
                // Should not occur, but just in case.
                setEffect {
                    CheckoutWizardContract.Effect.ShowToastMessage(resourceProvider.getGenericErrorMessage())
                }
            }
        }
    }

    private fun onStickyBottomButtonClicked() {
        navigateToStep(CheckoutWizardStepDirection.NEXT)
    }

    private fun onAlertDialogNegativeButtonClicked() {
        hideExitWizardAlertDialog()
    }

    private fun onAlertDialogPositiveButtonClicked() {
        val alertDialogAction = currentState.alertDialogAction ?: return

        hideExitWizardAlertDialog()

        when (alertDialogAction) {
            CheckoutWizardExitAlertDialogAction.EXIT_WIZARD -> {
                setEffect {
                    CheckoutWizardContract.Effect.Navigation.ExitWizard
                }
            }

            CheckoutWizardExitAlertDialogAction.NAVIGATE_TO_MARKETPLACE_SCREEN -> {
                setEffect {
                    CheckoutWizardContract.Effect.Navigation.NavigateToMarketplaceScreen
                }
            }
        }
    }

    private fun onShowToastMessage(message: String) {
        setEffect {
            CheckoutWizardContract.Effect.ShowToastMessage(message)
        }
    }

    private fun onActionNotSupported() {
        setEffect {
            CheckoutWizardContract.Effect.ShowToastMessage(resourceProvider.getString(R.string.screen_checkout_wizard_error_action_not_supported))
        }
    }

    private fun onNavigateToEditShippingDetailsScreen() {
        val newStep = CheckoutWizardStep.SHIPPING_DETAILS
        setState {
            copy(
                toolbarTitle = getToolbarTitle(newStep),
                wizardStep = newStep,
                stickyBottomButtonEnabled = true
            )
        }
    }

    private fun onNavigateToEditPaymentDetailsScreen() {
        val newStep = CheckoutWizardStep.PAYMENT_DETAILS
        setState {
            copy(
                toolbarTitle = getToolbarTitle(newStep),
                wizardStep = newStep,
                stickyBottomButtonEnabled = true
            )
        }
    }

    private fun onNavigateToCheckoutSuccessScreen(orderId: String) {
        setEffect {
            CheckoutWizardContract.Effect.Navigation.NavigateToCheckoutSuccessScreen(orderId)
        }
    }

    private fun onNavigateToOrderDetailsScreen(orderId: String) {
        setEffect {
            CheckoutWizardContract.Effect.Navigation.NavigateToOrderDetailsScreen(orderId)
        }
    }

    private fun onNavigateToMarketplaceScreen() {
        setAlertDialogAction(CheckoutWizardExitAlertDialogAction.NAVIGATE_TO_MARKETPLACE_SCREEN)
    }

    private fun onExitWizard() {
        setEffect {
            CheckoutWizardContract.Effect.Navigation.ExitWizard
        }
    }

    /**
     * Helper functions.
     */

    /**
     * Returns the toolbar title based on the wizard step.
     */
    private fun getToolbarTitle(step: CheckoutWizardStep): String {
        return resourceProvider.getString(R.string.screen_checkout_wizard_toolbar_title) + " " +
                resourceProvider.getString(
                    when (step) {
                        CheckoutWizardStep.SHIPPING_DETAILS -> R.string.screen_checkout_wizard_step_label_shipping_details
                        CheckoutWizardStep.PAYMENT_DETAILS -> R.string.screen_checkout_wizard_step_label_payment_details
                        CheckoutWizardStep.OVERVIEW -> R.string.screen_checkout_wizard_step_label_overview
                    }
                )
    }

    private fun setStickyBottomButtonEnabled(enabled: Boolean) {
        setState {
            copy(
                stickyBottomButtonEnabled = enabled
            )
        }
    }

    private fun hideExitWizardAlertDialog() {
        setAlertDialogAction(null)
    }

    private fun setAlertDialogAction(action: CheckoutWizardExitAlertDialogAction?) {
        setState {
            copy(
                alertDialogAction = action
            )
        }
    }

    private fun navigateToStep(wizardStepDirection: CheckoutWizardStepDirection) {
        val newStep = when (wizardStepDirection) {
            CheckoutWizardStepDirection.PREVIOUS -> currentState.wizardStep.previousStep
            CheckoutWizardStepDirection.NEXT -> currentState.wizardStep.nextStep
        }
        setState {
            copy(
                toolbarTitle = getToolbarTitle(step = newStep),
                wizardStep = newStep,
                stickyBottomButtonEnabled = wizardStepDirection == CheckoutWizardStepDirection.PREVIOUS
            )
        }
    }
}