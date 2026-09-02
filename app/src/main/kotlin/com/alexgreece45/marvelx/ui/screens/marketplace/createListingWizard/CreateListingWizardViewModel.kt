package com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.common_ui.snackbar.delegate.SnackbarDelegate
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarType
import com.alexgreece45.marvelx.controllers.createListingWizard.CreateListingWizardController
import com.alexgreece45.marvelx.core.data.helper.multi_part_builder.model.MultipartResult
import com.alexgreece45.marvelx.core.data.repositories.UserSessionRepository
import com.alexgreece45.marvelx.domain.helper.InteractorPartialState
import com.alexgreece45.marvelx.domain.interactors.marketplace.listing.ListingInteractor
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.UpdateListingDomain
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.components.model.ErrorButtonLayout
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.CreateListingWizardContract
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.CreateListingWizardErrorData
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.CreateListingWizardMode
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.CreateListingWizardStep
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.toDomain
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.navigation.CreateListingWizardRoute
import com.alexgreece45.marvelx.ui.screens.marketplace.mappers.ListingDetailsUiMapper
import com.alexgreece45.marvelx.ui.screens.marketplace.mappers.ListingUiMapper
import com.alexgreece45.marvelx.ui.screens.marketplace.model.UpdateListingBasicDetails
import com.alexgreece45.marvelx.util.ResourceProvider
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel(binds = [CreateListingWizardViewModel::class])
internal class CreateListingWizardViewModel(
    private val listingInteractor: ListingInteractor,
    private val resourceProvider: ResourceProvider,
    private val listingUiMapper: ListingUiMapper,
    private val createListingWizardController: CreateListingWizardController,
    private val listingDetailsUiMapper: ListingDetailsUiMapper,
    private val userSessionRepository: UserSessionRepository,
    savedStateHandle: SavedStateHandle,
    snackbarDelegate: SnackbarDelegate,
) : BaseViewModel<CreateListingWizardContract.Event, CreateListingWizardContract.State, CreateListingWizardContract.Effect>(),
    SnackbarDelegate by snackbarDelegate {

    init {
        init(savedStateHandle.toRoute<CreateListingWizardRoute>().listingId)
    }

    override fun setInitialState(): CreateListingWizardContract.State =
        CreateListingWizardContract.State()

    override fun handleEvents(event: CreateListingWizardContract.Event) {
        when (event) {
            CreateListingWizardContract.Event.OnBackButtonClicked -> onBackButtonClicked()
            CreateListingWizardContract.Event.OnCloseButtonClicked -> onCloseButtonClicked()
            CreateListingWizardContract.Event.OnStickyBottomButtonClicked -> onStickyBottomButtonClicked()

            is CreateListingWizardContract.Event.OnShowSnackbar -> onShowSnackbar(
                event.message,
                event.snackbarType
            )

            is CreateListingWizardContract.Event.SetNextButtonEnabled -> setNextButtonEnabled(event.enabled)

            CreateListingWizardContract.Event.AlertDialogEvents.OnNegativeButtonClicked -> onAlertDialogNegativeButtonClicked()
            CreateListingWizardContract.Event.AlertDialogEvents.OnPositiveButtonClicked -> onAlertDialogPositiveButtonClicked()
            CreateListingWizardContract.Event.OnExitWizard -> onExitWizard()
            is CreateListingWizardContract.Event.OnNavigateToListingDetailsScreen -> onNavigateToListingDetailsScreen(
                event.listingId
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        createListingWizardController.resetAllData()
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

            createListingWizardController.setWizardMode(wizardMode)

            getListing(listingId)
        } else {
            val wizardMode = CreateListingWizardMode.CREATE
            createListingWizardController.setWizardMode(wizardMode)

            setState {
                copy(
                    isLoading = false,
                    toolbarTitle = getToolbarTitle(
                        wizardMode = wizardMode,
                        wizardStep = wizardStep
                    ),
                    wizardMode = wizardMode
                )
            }
            setEffect {
                CreateListingWizardContract.Effect.InitBasicDetailsScreen(null)
            }
        }
    }

    private fun onBackButtonClicked() {
        if (currentState.wizardMode == null) {
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

            CreateListingWizardStep.ADVANCED_DETAILS, CreateListingWizardStep.OVERVIEW -> {
                // Should never occur, but just in case.
                showErroneousSnackbar(errorMessage = resourceProvider.getGenericErrorMessage())
                setEffect {
                    CreateListingWizardContract.Effect.Navigation.ExitWizard
                }
            }
        }
    }

    private fun onStickyBottomButtonClicked() {
        // Next step is success.
        if (currentState.wizardStep == CreateListingWizardStep.OVERVIEW) {
            onSuccessStepOperations()
            return
        }

        CreateListingWizardStep.entries.firstOrNull { it.order == currentState.wizardStep.order + 1 }
            ?.let { newStep ->
                val isNewStepOverview = newStep == CreateListingWizardStep.OVERVIEW
                if (isNewStepOverview) {
                    setState {
                        copy(
                            createListingUi = createListingWizardController.createListingUi
                        )
                    }
                }

                setState {
                    copy(
                        toolbarTitle = getToolbarTitle(
                            wizardMode = wizardMode,
                            wizardStep = newStep
                        ),
                        wizardStep = newStep,
                        isStickyBottomButtonEnabled = isNewStepOverview
                    )
                }
            }
    }

    private fun onShowSnackbar(message: String, snackbarType: SnackbarType) {
        showCustomSnackbar(message = message, type = snackbarType)
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

    private fun onExitWizard() {
        setEffect {
            CreateListingWizardContract.Effect.Navigation.ExitWizard
        }
    }

    private fun onNavigateToListingDetailsScreen(listingId: String) {
        setEffect {
            CreateListingWizardContract.Effect.Navigation.NavigateToListingDetailsScreen(
                listingId
            )
        }
    }

    /**
     * Helper functions.
     */

    /**
     *
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
                        currentUserId = userSessionRepository.getUserId()
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
                                createListingWizardController.setUpdateListing(listing)

                                with(listing) {
                                    UpdateListingBasicDetails(
                                        listingId = listing.listingId,
                                        title = title,
                                        description = description,
                                        categoryLabel = category.label,
                                        subcategoryLabel = subcategory.label,
                                        coverImageUrl = coverImageUrl,
                                        imageUrls = imageUrls
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
                CreateListingWizardStep.OVERVIEW -> R.string.screen_create_listing_wizard_screen_step_label_overview
            }
        )
    }

    private fun setNextButtonEnabled(enabled: Boolean) {
        setState {
            copy(
                isStickyBottomButtonEnabled = enabled
            )
        }
    }

    private fun onSuccessStepOperations() {
        if (currentState.wizardMode == CreateListingWizardMode.CREATE) {
            uploadDraftListing()
        } else {
            uploadModifiedListing()
        }
    }

    private fun uploadModifiedListing() {
        val listing = currentState.listingUi
        if (listing == null) {
            showErroneousSnackbar(
                errorMessage = resourceProvider.getString(R.string.screen_create_listing_wizard_screen_error_uploading_listing)
            )
            return
        }

        setState {
            copy(
                isLoading = true,
                error = null
            )
        }
        viewModelScope.launch {
            val wasModifiedListingUploaded = listingInteractor.updateListing(
                listingId = listing.listingId,
                updateListingDomain = UpdateListingDomain(
                    title = listing.title,
                    description = listing.description
                )
            )

            if (wasModifiedListingUploaded) {
                setEffect {
                    CreateListingWizardContract.Effect.Navigation.NavigateToCreateListingSuccessScreen(
                        listingId = listing.listingId,
                        wizardMode = CreateListingWizardMode.UPDATE
                    )
                }
            } else {
                setState {
                    copy(
                        isLoading = false
                    )
                }
                showErroneousSnackbar(errorMessage = resourceProvider.getString(R.string.screen_create_listing_wizard_screen_error_uploading_listing))
            }
        }
    }

    private fun uploadDraftListing() {
        val createListing = currentState.createListingUi
        if (createListing == null || createListing.listingDetails == null) {
            showErroneousSnackbar(errorMessage = resourceProvider.getString(R.string.screen_create_listing_wizard_screen_error_uploading_listing))
            return
        }

        viewModelScope.launch {
            val listingDetailsDomain = listingDetailsUiMapper.toDomain(createListing.listingDetails)
            if (listingDetailsDomain == null) {
                showErroneousSnackbar(errorMessage = resourceProvider.getString(R.string.screen_create_listing_wizard_screen_error_uploading_listing))
                return@launch
            }

            when (
                val createListingResult = listingInteractor.createListing(
                    createListingDomain = createListing.toDomain(
                        listingDetailsDomain
                    )
                )
            ) {
                is MultipartResult.GenericError,
                MultipartResult.NetworkError -> {
                    setState {
                        copy(
                            isLoading = false
                        )
                    }

                    showErroneousSnackbar(errorMessage = resourceProvider.getString(R.string.screen_create_listing_wizard_screen_error_uploading_listing))
                }

                MultipartResult.ImageProcessingError -> {
                    setState {
                        copy(
                            isLoading = false
                        )
                    }
                    showErroneousSnackbar(errorMessage = resourceProvider.getString(R.string.screen_create_listing_wizard_screen_error_reading_images))
                }

                is MultipartResult.Success -> {
                    createListingWizardController.resetCreateListingData()

                    val listingId = createListingResult.data
                    setEffect {
                        CreateListingWizardContract.Effect.Navigation.NavigateToCreateListingSuccessScreen(
                            listingId = listingId,
                            wizardMode = CreateListingWizardMode.CREATE
                        )
                    }
                }
            }
        }
    }
}