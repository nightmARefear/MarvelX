package com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard

import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.navigation.Screen
import com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.model.CheckoutWizardContract
import com.alexgreece45.marvelx.ui.screens.cart.checkoutWizard.model.CheckoutWizardStep
import com.alexgreece45.marvelx.util.ResourceProvider
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class CheckoutWizardViewModel(
    private val resourceProvider: ResourceProvider
) :
    BaseViewModel<CheckoutWizardContract.Event, CheckoutWizardContract.State, CheckoutWizardContract.Effect>() {

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
            is CheckoutWizardContract.Event.OnNavigationEvents.OrderSuccessScreen -> onNavigateToOrderSuccessScreen(
                event.orderId
            )

            is CheckoutWizardContract.Event.OnNavigationEvents.OrderDetailsScreen -> onNavigateToOrderDetailsScreen(
                event.orderId
            )

            is CheckoutWizardContract.Event.OnNavigationEvents.ExternalScreen -> onNavigateToExternalScreen(
                event.externalScreen
            )

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
        val currentStep = currentState.wizardStep
        if (currentStep == CheckoutWizardStep.SUCCESS) {
            setEffect {
                CheckoutWizardContract.Effect.Navigation.ExitWizard
            }
            return
        }

        when (val newStep =
            CheckoutWizardStep.entries.firstOrNull { step -> step.order == currentStep.order - 1 }) {
            null -> {
                setState {
                    copy(
                        exitWizardAlertDialogVisible = true
                    )
                }
            }

            else -> {
                setState {
                    copy(
                        toolbarTitle = getToolbarTitle(newStep),
                        wizardStep = newStep,
                        stickyBottomButtonEnabled = true
                    )
                }
            }
        }
    }

    private fun onCloseButtonClicked() {
        when (currentState.wizardStep) {
            CheckoutWizardStep.SHIPPING_DETAILS -> {
                setState {
                    copy(
                        exitWizardAlertDialogVisible = true
                    )
                }
            }

            CheckoutWizardStep.PAYMENT_DETAILS,
            CheckoutWizardStep.OVERVIEW -> {
                // Should not occur, but just in case.
                setEffect {
                    CheckoutWizardContract.Effect.ShowToastMessage(resourceProvider.getGenericErrorMessage())
                }
            }

            CheckoutWizardStep.SUCCESS -> {
                setEffect {
                    CheckoutWizardContract.Effect.Navigation.ExitWizard
                }
            }
        }
    }

    private fun onStickyBottomButtonClicked() {
        val currentStep = currentState.wizardStep
        if (currentStep == CheckoutWizardStep.SUCCESS) {
            setEffect {
                CheckoutWizardContract.Effect.Navigation.ExitWizard
            }
            return
        }

        when (val newStep =
            CheckoutWizardStep.entries.firstOrNull { step -> step.order == currentStep.order + 1 }) {
            null -> {
                setEffect {
                    CheckoutWizardContract.Effect.ShowToastMessage(resourceProvider.getGenericErrorMessage())
                }
            }

            else -> {
                setState {
                    copy(
                        toolbarTitle = getToolbarTitle(newStep),
                        wizardStep = newStep,
                        stickyBottomButtonEnabled = false
                    )
                }
            }
        }
    }

    private fun onAlertDialogNegativeButtonClicked() {
        hideExitWizardAlertDialog()
    }

    private fun onAlertDialogPositiveButtonClicked() {
        val screen = currentState.exitWizardAlertDialogPositiveActionScreen

        hideExitWizardAlertDialog()
        if (screen != null) {
            setEffect {
                CheckoutWizardContract.Effect.Navigation.NavigateToExternalScreen(screen)
            }
        } else {
            setEffect {
                CheckoutWizardContract.Effect.Navigation.ExitWizard
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

    private fun onNavigateToOrderSuccessScreen(orderId: String) {
        val newStep = CheckoutWizardStep.SUCCESS
        setState {
            copy(
                toolbarTitle = getToolbarTitle(newStep),
                wizardStep = newStep,
                stickyBottomButtonEnabled = true,
                orderId = orderId
            )
        }
    }

    private fun onNavigateToOrderDetailsScreen(orderId: String) {
        setEffect {
            CheckoutWizardContract.Effect.Navigation.NavigateToOrderDetailsScreen(orderId)
        }
    }

    private fun onNavigateToExternalScreen(externalScreen: Screen) {
        setState {
            copy(
                exitWizardAlertDialogVisible = true,
                exitWizardAlertDialogPositiveActionScreen = externalScreen
            )
        }
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
        if (step == CheckoutWizardStep.SUCCESS) return ""
        return resourceProvider.getString(R.string.screen_checkout_wizard_toolbar_title) + " " +
                resourceProvider.getString(
                    when (step) {
                        CheckoutWizardStep.SHIPPING_DETAILS -> R.string.screen_checkout_wizard_step_label_shipping_details
                        CheckoutWizardStep.PAYMENT_DETAILS -> R.string.screen_checkout_wizard_step_label_payment_details
                        CheckoutWizardStep.OVERVIEW -> R.string.screen_checkout_wizard_step_label_overview
                        CheckoutWizardStep.SUCCESS -> R.string.screen_checkout_wizard_step_label_success
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
        setState {
            copy(
                exitWizardAlertDialogVisible = false,
                exitWizardAlertDialogPositiveActionScreen = null
            )
        }
    }
}
