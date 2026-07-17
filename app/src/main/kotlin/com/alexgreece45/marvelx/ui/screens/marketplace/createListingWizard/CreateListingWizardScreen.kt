package com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
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
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction.Backable
import com.alexgreece45.marvelx.ui.components.WrapButton
import com.alexgreece45.marvelx.ui.components.effect.ObserveUiEffect
import com.alexgreece45.marvelx.ui.components.model.ErrorButtonType
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.advancedDetails.CreateListingAdvancedDetailsScreen
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.advancedDetails.CreateListingAdvancedDetailsViewModel
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.advancedDetails.model.CreateListingAdvancedDetailsScreenData.Create
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.advancedDetails.model.CreateListingAdvancedDetailsScreenData.Update
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.basicDetails.CreateListingBasicDetailsScreen
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.basicDetails.CreateListingBasicDetailsViewModel
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.basicDetails.model.CreateListingBasicDetailsContract.Event.Init
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.CreateListingWizardContract
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.CreateListingWizardContract.Event.SetNextButtonEnabled
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.CreateListingWizardMode
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.CreateListingWizardStep
import com.alexgreece45.marvelx.ui.screens.marketplace.listingDetailsScreen.ListingDetailsScreen
import com.alexgreece45.marvelx.ui.screens.marketplace.listingDetailsScreen.ListingDetailsViewModel
import com.alexgreece45.marvelx.ui.screens.marketplace.listingDetailsScreen.model.ListingDetailsContract
import com.alexgreece45.marvelx.ui_theme.theme.values.customColors
import com.alexgreece45.marvelx.util.extensions.showToast
import kotlinx.coroutines.flow.Flow
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun CreateListingWizardScreen(
    onEvent: (CreateListingWizardContract.Event) -> Unit,
    state: CreateListingWizardContract.State,
    effect: Flow<CreateListingWizardContract.Effect>,
    onNavigationEffect: (CreateListingWizardContract.Effect.Navigation) -> Unit,
    listingId: String?,
    createListingWizardViewModel: CreateListingWizardViewModel = koinViewModel(),
    createListingBasicDetailsViewModel: CreateListingBasicDetailsViewModel = koinViewModel(),
    createListingAdvancedDetailsViewModel: CreateListingAdvancedDetailsViewModel = koinViewModel()
) {

    val context = LocalContext.current
    val createListingWizardState =
        createListingWizardViewModel.uiState.collectAsStateWithLifecycle().value
    val createListingBasicDetailsState =
        createListingBasicDetailsViewModel.uiState.collectAsStateWithLifecycle().value

    ObserveUiEffect(effect) { uiEffect ->
        when (uiEffect) {
            is CreateListingWizardContract.Effect.Navigation -> onNavigationEffect(uiEffect)
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
            CreateListingWizardStep.BASIC_DETAILS,
            CreateListingWizardStep.ADVANCED_DETAILS,
            CreateListingWizardStep.OVERVIEW -> {
                Backable(
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
        stickyBottom = {
            WrapButton(
                onClick = { onEvent(CreateListingWizardContract.Event.OnStickyBottomButtonClicked) },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.isStickyBottomButtonEnabled
            ) {
                Text(
                    text = stringResource(
                        if (state.wizardStep == CreateListingWizardStep.OVERVIEW) {
                            if (state.wizardMode == CreateListingWizardMode.CREATE) {
                                R.string.screen_create_listing_wizard_screen_sticky_bottom_button_upload_new_listing
                            } else {
                                R.string.screen_create_listing_wizard_screen_sticky_bottom_button_upload_modified_listing
                            }
                        } else {
                            R.string.screen_create_listing_wizard_screen_sticky_bottom_button_next
                        }
                    )
                )
            }
        }
    ) { _ ->
        Column(modifier = Modifier.fillMaxSize()) {
            LinearProgressIndicator(
                progress = state.progress,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.customColors.genericRed
            )

            when (state.wizardStep) {
                CreateListingWizardStep.BASIC_DETAILS -> {
                    CreateListingBasicDetailsScreen(
                        onEvent = createListingBasicDetailsViewModel::setEvent,
                        state = createListingBasicDetailsState,
                        effect = createListingBasicDetailsViewModel.uiEffect,
                        showToastMessage = { message ->
                            onEvent(
                                CreateListingWizardContract.Event.OnShowToastMessage(
                                    message
                                )
                            )
                        },
                        goToPreviousStep = { onEvent(CreateListingWizardContract.Event.OnBackButtonClicked) },
                        setNextButtonEnabled = { enabled ->
                            onEvent(
                                SetNextButtonEnabled(
                                    enabled
                                )
                            )
                        }
                    )
                }

                CreateListingWizardStep.ADVANCED_DETAILS -> {
                    val listingUi = state.listingUi

                    CreateListingAdvancedDetailsScreen(
                        onEvent = createListingAdvancedDetailsViewModel::setEvent,
                        state = createListingAdvancedDetailsViewModel.uiState.collectAsStateWithLifecycle().value,
                        effect = createListingAdvancedDetailsViewModel.uiEffect,
                        showToastMessage = { message ->
                            onEvent(
                                CreateListingWizardContract.Event.OnShowToastMessage(
                                    message
                                )
                            )
                        },
                        goToPreviousStep = { onEvent(CreateListingWizardContract.Event.OnBackButtonClicked) },
                        setNextButtonEnabled = { enabled ->
                            onEvent(
                                SetNextButtonEnabled(
                                    enabled
                                )
                            )
                        },
                        createListingAdvancedDetailsScreenData = if (listingUi == null) Create(
                            categoryCode = createListingBasicDetailsState.category.value?.code
                        ) else Update(
                            listingDetails = listingUi.details,
                            condition = listingUi.condition,
                            price = listingUi.price.value
                        )
                    )
                }

                CreateListingWizardStep.OVERVIEW -> {
                    val listingDetailsViewModel = koinViewModel<ListingDetailsViewModel>()

                    ListingDetailsScreen(
                        onEvent = listingDetailsViewModel::setEvent,
                        state = listingDetailsViewModel.uiState.collectAsStateWithLifecycle().value,
                        effect = listingDetailsViewModel.uiEffect,
                        onNavigation = { navigationEffect ->
                            when (navigationEffect) {
                                ListingDetailsContract.Effect.Navigation.GoBack -> {
                                    onEvent(CreateListingWizardContract.Event.OnBackButtonClicked)
                                }

                                is ListingDetailsContract.Effect.Navigation.NavigateToEditListingScreen -> {}
                                is ListingDetailsContract.Effect.Navigation.NavigateToProfileDetailsScreen -> {}
                                ListingDetailsContract.Effect.Navigation.GoToNextScreen -> onEvent(
                                    CreateListingWizardContract.Event.OnStickyBottomButtonClicked
                                )
                            }
                        },
                        listingId = null,
                        createListingWizardMode = createListingWizardState.wizardMode,
                        isInnerScreen = true
                    )
                }
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