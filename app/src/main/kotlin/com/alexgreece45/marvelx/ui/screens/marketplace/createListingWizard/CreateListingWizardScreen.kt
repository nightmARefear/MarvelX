package com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.components.AlertDialogButton
import com.alexgreece45.marvelx.ui.components.AlertDialogError
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.WrapButton
import com.alexgreece45.marvelx.ui.components.effect.ObserveUiEffect
import com.alexgreece45.marvelx.ui.components.model.ErrorButtonType
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.advancedDetails.CreateListingAdvancedDetailsScreen
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.advancedDetails.CreateListingAdvancedDetailsViewModel
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.advancedDetails.model.CreateListingAdvancedDetailsScreenData
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.basicDetails.CreateListingBasicDetailsScreen
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.basicDetails.CreateListingBasicDetailsViewModel
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.basicDetails.model.CreateListingBasicDetailsContract
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.basicDetails.model.CreateListingBasicDetailsContract.Event.*
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.CreateListingWizardContract
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.CreateListingWizardMode
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.CreateListingWizardStep
import com.alexgreece45.marvelx.util.extensions.showToast
import kotlinx.coroutines.flow.Flow
import org.koin.androidx.compose.koinViewModel

@Composable
fun CreateListingWizardScreen(
    onEvent: (CreateListingWizardContract.Event) -> Unit,
    state: CreateListingWizardContract.State,
    effect: Flow<CreateListingWizardContract.Effect>,
    exitWizard: () -> Unit,
    listingId: String?,
    createListingBasicDetailsViewModel: CreateListingBasicDetailsViewModel = koinViewModel(),
    createListingAdvancedDetailsViewModel: CreateListingAdvancedDetailsViewModel = koinViewModel()
) {

    val context = LocalContext.current

    ObserveUiEffect(effect) { uiEffect ->
        when (uiEffect) {
            CreateListingWizardContract.Effect.Navigation.ExitWizard -> exitWizard()
            is CreateListingWizardContract.Effect.InitBasicDetailsScreen -> {
                createListingBasicDetailsViewModel.setEvent(
                    Init(uiEffect.updateListingBasicDetails)
                )
            }

            is CreateListingWizardContract.Effect.ShowToastMessage -> {
                context.showToast(uiEffect.message)
            }
        }
    }

    LaunchedEffect(Unit) {
        onEvent(CreateListingWizardContract.Event.Init(listingId))
    }

    ContentScreen(
        isLoading = state.isLoading,
        navigableAction = when (state.wizardStep) {
            CreateListingWizardStep.BASIC_DETAILS, CreateListingWizardStep.SUCCESS_CREATION -> {
                ScreenNavigableAction.Closeable(
                    onClose = {
                        onEvent(CreateListingWizardContract.Event.OnCloseButtonClicked)
                    }
                )
            }

            CreateListingWizardStep.ADVANCED_DETAILS,
            CreateListingWizardStep.LOGISTICS -> {
                ScreenNavigableAction.Backable(
                    onBack = {
                        onEvent(CreateListingWizardContract.Event.OnBackButtonClicked)
                    }
                )
            }
        },
        onBack = {
            onEvent(CreateListingWizardContract.Event.OnBackButtonClicked)
        },
        showToolbar = true,
        toolbarTitle = state.toolbarTitle,
        toolbarActions = listOf(),
        stickyBottom = {
            WrapButton(
                onClick = { onEvent(CreateListingWizardContract.Event.OnNextButtonClicked) },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.isNextButtonEnabled
            ) {
                Text(text = stringResource(R.string.screen_create_listing_wizard_screen_button_next))
            }
        }
    ) { _ ->
        Column(modifier = Modifier.fillMaxSize()) {
            // TODO: Add progress bar.
            LinearProgressIndicator(
                progress = state.wizardStep.order.toFloat() / 4f,
                modifier = Modifier.fillMaxWidth()
            )

            when (state.wizardStep) {
                CreateListingWizardStep.BASIC_DETAILS -> {
                    CreateListingBasicDetailsScreen(
                        onEvent = createListingBasicDetailsViewModel::setEvent,
                        state = createListingBasicDetailsViewModel.uiState.collectAsStateWithLifecycle().value,
                        effect = createListingBasicDetailsViewModel.uiEffect,
                        goToPreviousStep = { onEvent(CreateListingWizardContract.Event.OnBackButtonClicked) },
                        setNextButtonEnabled = {
                            onEvent(
                                CreateListingWizardContract.Event.SetNextButtonEnabled(
                                    it
                                )
                            )
                        }
                    )
                }

                CreateListingWizardStep.ADVANCED_DETAILS -> {
                    val createListingBasicDetailsState =
                        createListingBasicDetailsViewModel.uiState.collectAsStateWithLifecycle().value
                    val listingUi = state.listingUi

                    CreateListingAdvancedDetailsScreen(
                        onEvent = createListingAdvancedDetailsViewModel::setEvent,
                        state = createListingAdvancedDetailsViewModel.uiState.collectAsStateWithLifecycle().value,
                        effect = createListingAdvancedDetailsViewModel.uiEffect,
                        goToPreviousStep = { onEvent(CreateListingWizardContract.Event.OnBackButtonClicked) },
                        setNextButtonEnabled = {
                            onEvent(
                                CreateListingWizardContract.Event.SetNextButtonEnabled(
                                    it
                                )
                            )
                        },
                        createListingAdvancedDetailsScreenData = if (listingUi == null) CreateListingAdvancedDetailsScreenData.Create(
                            categoryCode = createListingBasicDetailsState.category.value?.code
                        ) else CreateListingAdvancedDetailsScreenData.Update(
                            listingDetails = listingUi.details,
                            condition = listingUi.condition,
                            price = listingUi.price
                        )
                    )
                }

                CreateListingWizardStep.LOGISTICS -> {}
                CreateListingWizardStep.SUCCESS_CREATION -> {}
            }

            AlertDialogError(
                isVisible = state.isAlertDialogVisible,
                title = stringResource(
                    if (state.wizardMode == CreateListingWizardMode.CREATE) R.string.screen_create_listing_wizard_screen_alert_dialog_title_create else R.string.screen_create_listing_wizard_screen_alert_dialog_title_update
                ),
                errorMessage = { Text(text = stringResource(if (state.wizardMode == CreateListingWizardMode.CREATE) R.string.screen_create_listing_wizard_screen_alert_dialog_message_create else R.string.screen_create_listing_wizard_screen_alert_dialog_message_update)) },
                confirmButton = AlertDialogButton(
                    label = stringResource(R.string.screen_create_listing_wizard_screen_alert_dialog_button_positive),
                    buttonType = ErrorButtonType.Contained,
                    onClick = { onEvent(CreateListingWizardContract.Event.AlertDialogEvents.OnPositiveButtonClicked) }
                ),
                dismissButton = AlertDialogButton(
                    label = stringResource(R.string.screen_create_listing_wizard_screen_alert_dialog_button_negative),
                    onClick = { onEvent(CreateListingWizardContract.Event.AlertDialogEvents.OnNegativeButtonClicked) }
                )
            )
        }
    }
}
