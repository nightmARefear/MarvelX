package com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.controllers.PrefKeys
import com.alexgreece45.marvelx.domain.helper.InteractorPartialState
import com.alexgreece45.marvelx.domain.interactors.marketplace.listing.ListingInteractor
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.components.model.ErrorButtonLayout
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.CreateListingWizardContract
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.CreateListingWizardErrorData
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.CreateListingWizardMode
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.CreateListingWizardStep
import com.alexgreece45.marvelx.ui.screens.marketplace.mappers.ListingUiMapper
import com.alexgreece45.marvelx.ui.screens.marketplace.model.UpdateListingBasicDetails
import com.alexgreece45.marvelx.util.ResourceProvider
import kotlinx.coroutines.launch

class CreateListingWizardViewModel(
    private val listingInteractor: ListingInteractor,
    private val resourceProvider: ResourceProvider,
    private val listingUiMapper: ListingUiMapper,
    private val prefKeys: PrefKeys
) : BaseViewModel<CreateListingWizardContract.Event, CreateListingWizardContract.State, CreateListingWizardContract.Effect>() {

    override fun setInitialState(): CreateListingWizardContract.State =
        CreateListingWizardContract.State()

    override fun handleEvents(event: CreateListingWizardContract.Event) {
        when (event) {
            is CreateListingWizardContract.Event.Init -> init(event.listingId)
            CreateListingWizardContract.Event.OnBackButtonClicked -> onBackButtonClicked()
            CreateListingWizardContract.Event.OnCloseButtonClicked -> onCloseButtonClicked()
            CreateListingWizardContract.Event.OnNextButtonClicked -> onNextButtonClicked()
            is CreateListingWizardContract.Event.SetNextButtonEnabled -> setNextButtonEnabled(event.enabled)
            CreateListingWizardContract.Event.AlertDialogEvents.OnNegativeButtonClicked -> onAlertDialogNegativeButtonClicked()
            CreateListingWizardContract.Event.AlertDialogEvents.OnPositiveButtonClicked -> onAlertDialogPositiveButtonClicked()
        }
    }

    private fun init(listingId: String?) {
        val wizardStep = currentState.wizardStep

        if (!listingId.isNullOrBlank()) {
            val wizardMode = CreateListingWizardMode.UPDATE
            setState {
                copy(
                    toolbarTitle = getToolbarTitle(
                        wizardMode = wizardMode,
                        wizardStep = wizardStep
                    ),
                    wizardMode = wizardMode
                )
            }
            getListing(listingId)
        } else {
            val wizardMode = CreateListingWizardMode.CREATE
            setState {
                copy(
                    isLoading = false,
                    toolbarTitle = getToolbarTitle(
                        wizardMode = wizardMode,
                        wizardStep = wizardStep
                    ),
                    wizardMode = CreateListingWizardMode.CREATE
                )
            }
            setEffect {
                CreateListingWizardContract.Effect.InitBasicDetailsScreen(null)
            }
        }
    }

    private fun onBackButtonClicked() {
        if (currentState.wizardMode == null || currentState.wizardStep == CreateListingWizardStep.SUCCESS_CREATION) {
            setEffect {
                CreateListingWizardContract.Effect.Navigation.ExitWizard
            }
            return
        }

        when (val newStep =
            CreateListingWizardStep.entries.firstOrNull { it.order == currentState.wizardStep.order - 1 }) {
            null -> {
                setState {
                    copy(
                        isAlertDialogVisible = true
                    )
                }
            }

            else -> {
                setState {
                    copy(
                        toolbarTitle = getToolbarTitle(
                            wizardMode = wizardMode,
                            wizardStep = newStep
                        ),
                        wizardStep = newStep
                    )
                }
            }
        }
    }

    private fun onCloseButtonClicked() {
        when (currentState.wizardStep) {
            CreateListingWizardStep.BASIC_DETAILS -> {
                setState {
                    copy(
                        isAlertDialogVisible = true
                    )
                }
            }

            CreateListingWizardStep.ADVANCED_DETAILS,
            CreateListingWizardStep.LOGISTICS -> {
                // Should never occur, but just in case.
                setEffect {
                    CreateListingWizardContract.Effect.ShowToastMessage(resourceProvider.getGenericErrorMessage())
                }
                setEffect {
                    CreateListingWizardContract.Effect.Navigation.ExitWizard
                }
            }

            CreateListingWizardStep.SUCCESS_CREATION -> {
                setEffect {
                    CreateListingWizardContract.Effect.Navigation.ExitWizard
                }
            }
        }
    }

    private fun onNextButtonClicked() {
        CreateListingWizardStep.entries.firstOrNull { it.order == currentState.wizardStep.order + 1 }
            ?.let { newStep ->
                setState {
                    copy(
                        toolbarTitle = getToolbarTitle(
                            wizardMode = wizardMode,
                            wizardStep = newStep
                        ),
                        wizardStep = newStep,
                        isNextButtonEnabled = false
                    )
                }
            }
    }

    private fun setNextButtonEnabled(enabled: Boolean) {
        setState {
            copy(
                isNextButtonEnabled = enabled
            )
        }
    }

    private fun onAlertDialogNegativeButtonClicked() {
        setState {
            copy(
                isAlertDialogVisible = false
            )
        }
    }

    private fun onAlertDialogPositiveButtonClicked() {
        setState {
            copy(
                isAlertDialogVisible = false
            )
        }
        setEffect {
            CreateListingWizardContract.Effect.Navigation.ExitWizard
        }
    }

    /**
     * Helper functions.
     */

    private fun getListing(listingId: String) {
        setState {
            copy(
                isLoading = true,
                error = null
            )
        }
        viewModelScope.launch {
            when (val partialState = listingInteractor.getListing(listingId)) {
                is InteractorPartialState.Error,
                is InteractorPartialState.Failed,
                is InteractorPartialState.NoInternetConnection -> {
                    setState {
                        copy(
                            isLoading = false,
                            error = CreateListingWizardErrorData(
                                label = resourceProvider.getString(R.string.generic_error),
                                description = resourceProvider.getString(
                                    if (partialState is InteractorPartialState.NoInternetConnection) R.string.no_internet_connection_error_message else R.string.generic_unknown_error
                                ),
                                errorButtonLayout = ErrorButtonLayout.Single(
                                    primaryButtonLabel = resourceProvider.getString(R.string.generic_retry_button),
                                    onPrimaryButtonClicked = { getListing(listingId) }
                                )
                            )
                        )
                    }
                }

                is InteractorPartialState.Success -> {
                    val listingUi = listingUiMapper.toUi(
                        listing = partialState.data,
                        currentUserId = prefKeys.getUserId()
                    )

                    setState {
                        copy(
                            isLoading = false,
                            listingUi = listingUi,
                            error = null
                        )
                    }

                    setEffect {
                        CreateListingWizardContract.Effect.InitBasicDetailsScreen(
                            updateListingBasicDetails = listingUi?.let { listing ->
                                with(listing) {
                                    UpdateListingBasicDetails(
                                        listingId = listing.listingId,
                                        title = title,
                                        description = description,
                                        categoryLabel = category.label,
                                        subcategoryLabel = subcategory.label,
                                        imageUrls = allImageUrls
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    private fun getToolbarTitle(
        wizardMode: CreateListingWizardMode?,
        wizardStep: CreateListingWizardStep
    ): String {
        return resourceProvider.getString(
            if (wizardMode == CreateListingWizardMode.CREATE) {
                R.string.screen_create_listing_wizard_screen_toolbar_title_create
            } else R.string.screen_create_listing_wizard_screen_toolbar_title_update
        ) + " " + resourceProvider.getString(
            when (wizardStep) {
                CreateListingWizardStep.BASIC_DETAILS -> R.string.screen_create_listing_wizard_screen_step_label_basic
                CreateListingWizardStep.ADVANCED_DETAILS -> R.string.screen_create_listing_wizard_screen_step_label_advanced
                CreateListingWizardStep.LOGISTICS -> R.string.screen_create_listing_wizard_screen_step_label_logistics
                CreateListingWizardStep.SUCCESS_CREATION -> R.string.screen_create_listing_wizard_screen_step_label_basic // TODO: To be reviewed.
            }
        )
    }
}
