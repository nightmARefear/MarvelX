package com.alexgreece45.marvelx.ui.screens.profile

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.common_ui.imageValidator.ImageValidator
import com.alexgreece45.marvelx.common_ui.imageValidator.model.ImageValidationConfig
import com.alexgreece45.marvelx.common_ui.imageValidator.model.ImageValidationError
import com.alexgreece45.marvelx.common_ui.imageValidator.model.SingleImageValidationResult
import com.alexgreece45.marvelx.common_ui.snackbar.delegate.SnackbarDelegate
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarActionData
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarActionType
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarNavigationPayload
import com.alexgreece45.marvelx.core.common.config.ConfigLogic
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import com.alexgreece45.marvelx.core.data.helper.multi_part_builder.model.MultipartResult
import com.alexgreece45.marvelx.core.data.repositories.UserSessionRepository
import com.alexgreece45.marvelx.domain.interactors.GetCurrentUserProfileDetailsPartialState
import com.alexgreece45.marvelx.domain.interactors.UserInteractor
import com.alexgreece45.marvelx.model.model.generic.AttachedFileContentData
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.components.AlertDialogButton
import com.alexgreece45.marvelx.ui.components.IconData
import com.alexgreece45.marvelx.ui.components.model.BottomSheetContentItem
import com.alexgreece45.marvelx.ui.components.model.BottomSheetContentItemIconData
import com.alexgreece45.marvelx.ui.components.model.ErrorButtonType
import com.alexgreece45.marvelx.ui.components.sections.model.SectionHeaderData
import com.alexgreece45.marvelx.ui.screens.profile.model.ProfileBottomSheetType
import com.alexgreece45.marvelx.ui.screens.profile.model.ProfileContract
import com.alexgreece45.marvelx.ui.screens.profile.model.ProfileErrorAlertDialog
import com.alexgreece45.marvelx.ui.screens.profile.model.ProfileScreenErrorView
import com.alexgreece45.marvelx.util.FileUtil
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.extensions.toSafeUrlEncoding
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import java.util.UUID

@KoinViewModel(binds = [ProfileViewModel::class])
class ProfileViewModel(
    private val appLogger: AppLogger,
    private val resourceProvider: ResourceProvider,
    private val userInteractor: UserInteractor,
    private val configLogic: ConfigLogic,
    private val userSessionRepository: UserSessionRepository,
    private val imageValidator: ImageValidator,
    snackbarDelegate: SnackbarDelegate,
) : BaseViewModel<ProfileContract.Event, ProfileContract.State, ProfileContract.Effect>(),
    SnackbarDelegate by snackbarDelegate {

    override fun setInitialState(): ProfileContract.State = ProfileContract.State()

    private val loggerTag = "ProfileViewModel"

    override fun handleEvents(event: ProfileContract.Event) {
        when (event) {
            is ProfileContract.Event.Init -> {
                if (event.deepLinkAction == null) {
                    init(event)
                    return
                }

                setEffect {
                    ProfileContract.Effect.OpenDeepLinkAction(event.deepLinkAction)
                }
            }

            is ProfileContract.Event.OnSnackbarNavigationPayloadReceived -> {
                onSnackbarNavigationPayloadReceived(event.snackbarNavigationPayload)
            }

            ProfileContract.Event.OnSettingsIconButtonClicked -> {
                setEffect {
                    ProfileContract.Effect.Navigation.NavigateToSettingsScreen
                }
            }

            ProfileContract.Event.OnSystemBack -> {
                when {
                    currentState.bottomSheetType != null -> hideBottomSheet()
                    currentState.croppedImageFileContentData != null -> exitCroppedImageScreen()
                    else -> setEffect {
                        ProfileContract.Effect.Navigation.NavigateUp
                    }
                }
            }

            ProfileContract.Event.OnErrorClose -> {
                setState {
                    copy(
                        error = null
                    )
                }
            }

            ProfileContract.Event.OnSeeYourProfileClicked -> {
                viewModelScope.launch {
                    val userId = userSessionRepository.getUserId()
                    setEffect {
                        ProfileContract.Effect.Navigation.NavigateToProfileDetailsScreen(
                            userId
                        )
                    }
                }
            }

            ProfileContract.Event.ProfileImage.OnLoaded -> {
                setState {
                    copy(
                        isLoading = false
                    )
                }
            }

            ProfileContract.Event.ProfileImage.OnClicked -> {
                showBottomSheet(ProfileBottomSheetType.ProfileImage)
            }

            ProfileContract.Event.ProfileImage.BottomSheet.OnUploadImageClicked -> {
                hideBottomSheet()
                setEffect {
                    ProfileContract.Effect.OpenPhotoPicker
                }
            }

            ProfileContract.Event.ProfileImage.BottomSheet.OnViewImageClicked -> {
                hideBottomSheet()

                setEffect {
                    ProfileContract.Effect.Navigation.NavigateToProfileImageScreen(
                        currentState.profileDetails?.profileImageUrlData?.imageUrl
                            .orEmpty()
                            .toSafeUrlEncoding("")
                    )
                }
            }

            is ProfileContract.Event.ProfileImage.ImageSelectionEvent.OnSuccess -> {
                val imageFileContentData = event.imageFileContentData

                val imageValidationResult = imageValidator.validateImage(
                    image = imageFileContentData,
                    config = ImageValidationConfig(
                        allowedExtensions = configLogic.supportedImageFileExtensions,
                        maxSizeMB = configLogic.imageFileMaxSize.toInt()
                    )
                )
                when (imageValidationResult) {
                    is SingleImageValidationResult.Success -> {
                        val image = imageValidationResult.image

                        appLogger.d(loggerTag) { "(OnImageSelected) selected image URI: ${image.uri}" }
                        setEffect {
                            ProfileContract.Effect.OpenImageCropper(
                                toolbarTitle = resourceProvider.getString(R.string.profile_screen_cropped_cropped_image_toolbar_title),
                                minImageSize = 350,
                                maxImageSize = 1250,
                                imageUri = image.uri
                            )
                        }
                    }

                    is SingleImageValidationResult.Error -> {
                        val errorMessage = when (imageValidationResult.error) {
                            ImageValidationError.OverMaxSize -> resourceProvider.getString(
                                R.string.screen_profile_error_image_select_too_big,
                                FileUtil.getMegabytesFromBytes(configLogic.imageFileMaxSize)
                            )

                            ImageValidationError.InvalidExtensionType -> resourceProvider.getString(
                                R.string.screen_profile_error_image_select_invalid_extension
                            )

                            ImageValidationError.AttachmentError -> resourceProvider.getString(R.string.screen_profile_error_image_select_reading)
                        }

                        showErroneousSnackbar(errorMessage = errorMessage)
                    }
                }
            }

            is ProfileContract.Event.ProfileImage.ImageSelectionEvent.NoSelection -> {}

            is ProfileContract.Event.ProfileImage.ImageCropEvent.OnSuccess -> {
                if (event.imageFileContentData == null) {
                    appLogger.e(loggerTag) { "Error cropping image!" }

                    showErroneousSnackbar(
                        errorMessage = resourceProvider.getString(R.string.screen_profile_error_image_crop)
                    )
                    setState {
                        copy(
                            croppedImageIsLoading = false,
                            croppedImageFileContentData = null
                        )
                    }
                    return
                }

                appLogger.d(loggerTag) { "Image was successfully cropped!" }
                setState {
                    copy(
                        croppedImageFileContentData = event.imageFileContentData
                    )
                }
            }

            is ProfileContract.Event.ProfileImage.ImageCropEvent.OnFailure -> {
                if (event.errorMessage?.contains("cancelled") == false) {
                    appLogger.e(loggerTag) { "Error cropping image!" }

                    showErroneousSnackbar(
                        errorMessage = resourceProvider.getString(R.string.screen_profile_error_image_crop)
                    )
                    setState {
                        copy(
                            croppedImageIsLoading = false,
                            croppedImageFileContentData = null
                        )
                    }
                }
            }

            ProfileContract.Event.ProfileImage.CroppedImage.OnCloseClicked -> {
                exitCroppedImageScreen()
            }

            ProfileContract.Event.ProfileImage.CroppedImage.OnSaveClicked -> {
                onSaveButtonClicked()
            }
        }
    }

    // Event functions.

    private fun onSnackbarNavigationPayloadReceived(snackbarNavigationPayload: SnackbarNavigationPayload) {
        showCustomSnackbar(snackbarNavigationPayload)
    }

    /**
     * Initializes the screen by performing the appropriate operations.
     */
    private fun init(event: ProfileContract.Event) {
        setState {
            copy(
                isLoading = true
            )
        }

        initializeSections()
        initializeProfileImageBottomSheetContentItems()

        // Load profile details.
        viewModelScope.launch {
            userInteractor.getCurrentUserBasicProfileDetails().collect {
                when (it) {
                    is GetCurrentUserProfileDetailsPartialState.Success -> {
                        setState {
                            copy(
                                profileBannerLabel = resourceProvider.getString(
                                    R.string.profile_screen_profile_banner_label_welcome_back,
                                    it.basicProfileDetails.firstName
                                ),
                                profileDetails = it.basicProfileDetails
                            )
                        }
                    }

                    is GetCurrentUserProfileDetailsPartialState.Failed -> {
                        setState {
                            copy(
                                isLoading = false,
                                profileBannerLabel = resourceProvider.getString(
                                    R.string.profile_screen_profile_banner_label_welcome_back,
                                    ""
                                )
                            )
                        }
                        showError(
                            errorSubtitle = it.errorMessage,
                            event = event
                        )
                    }

                    GetCurrentUserProfileDetailsPartialState.NoInternetConnection -> {
                        setState {
                            copy(
                                isLoading = false,
                                profileBannerLabel = resourceProvider.getString(
                                    R.string.profile_screen_profile_banner_label_welcome_back,
                                    ""
                                )
                            )
                        }
                        showError(
                            errorSubtitle = resourceProvider.getNoInternetConnectionMessage(),
                            event = event
                        )
                    }
                }
            }
        }
    }

    // Helper functions.

    /**
     * Initializes the sections.
     */
    private fun initializeSections() {
        setState {
            copy(
                isLoading = false,
                sections = listOf(
                    SectionHeaderData(
                        title = resourceProvider.getString(R.string.profile_screen_section_label_scan_qr_code),
                        iconData = IconData(
                            iconResource = R.drawable.ic_scan_qr_code,
                            contentDescription = R.string.content_description_scan_qr_code
                        ),
                        isNavigable = false,
                        onClick = {
                            setEffect {
                                ProfileContract.Effect.Navigation.NavigateToQrCodeScreen("profileScreen") // TODO: FIX!
                            }
                        }
                    ),
                    SectionHeaderData(
                        title = resourceProvider.getString(R.string.profile_screen_section_label_friends),
                        iconData = IconData(
                            iconResource = R.drawable.ic_friends,
                            contentDescription = R.string.content_description_friends
                        ),
                        isNavigable = true,
                        onClick = {
                            setEffect {
                                ProfileContract.Effect.Navigation.NavigateToFriendsScreen
                            }
                        }
                    ),
                    SectionHeaderData(
                        title = resourceProvider.getString(R.string.profile_screen_section_label_orders),
                        iconData = IconData(
                            iconResource = R.drawable.ic_orders,
                            contentDescription = R.string.screen_profile_content_description_orders_icon
                        ),
                        isNavigable = true,
                        onClick = {
                            setEffect {
                                ProfileContract.Effect.Navigation.NavigateToOrdersScreen
                            }
                        }
                    ),
                    SectionHeaderData(
                        title = resourceProvider.getString(R.string.profile_screen_section_label_my_listings),
                        iconData = IconData(
                            iconResource = R.drawable.ic_listings,
                            contentDescription = R.string.content_description_friends
                        ),
                        isNavigable = true,
                        onClick = {
                            setEffect {
                                ProfileContract.Effect.Navigation.NavigateToMyListingsScreen
                            }
                        }
                    ),
                )
            )
        }
    }

    /**
     * Initializes the bottom sheet content items for the `profile image` bottom sheet.
     */
    private fun initializeProfileImageBottomSheetContentItems() {
        setState {
            copy(
                profileImageBottomSheetContentItems = listOf(
                    BottomSheetContentItem(
                        title = resourceProvider.getString(R.string.profile_screen_bottom_sheet_label_view_profile_image),
                        leadingIconData = BottomSheetContentItemIconData(
                            iconData = IconData(
                                iconResource = R.drawable.ic_image,
                                contentDescription = R.string.content_description_view_profile_image,
                            ),
                            showBackgroundColor = true,
                        ),
                        onClickEvent = ProfileContract.Event.ProfileImage.BottomSheet.OnViewImageClicked
                    ),
                    BottomSheetContentItem(
                        title = resourceProvider.getString(R.string.profile_screen_bottom_sheet_label_upload_profile_image),
                        leadingIconData = BottomSheetContentItemIconData(
                            iconData = IconData(
                                iconResource = R.drawable.ic_upload,
                                contentDescription = R.string.content_description_upload_profile_image,
                            ),
                            showBackgroundColor = true,
                        ),
                        onClickEvent = ProfileContract.Event.ProfileImage.BottomSheet.OnUploadImageClicked
                    ),
                )
            )
        }
    }

    /**
     * Shows the error view.
     */
    private fun showError(
        errorTitle: String? = null,
        errorSubtitle: String,
        event: ProfileContract.Event
    ) {
        setState {
            copy(
                error = ProfileScreenErrorView(
                    errorTitle = errorTitle
                        ?: resourceProvider.getString(R.string.profile_screen_error_loading_profile_details),
                    errorSubtitle = errorSubtitle,
                    event = event
                )
            )
        }
    }

    /**
     * Shows the bottom sheet.
     */
    private fun showBottomSheet(bottomSheetType: ProfileBottomSheetType) {
        setState {
            copy(
                bottomSheetType = bottomSheetType
            )
        }
        setEffect {
            ProfileContract.Effect.BottomSheet.Show
        }
    }

    /**
     * Hides the bottom sheet.
     */
    private fun hideBottomSheet() {
        setState {
            copy(
                bottomSheetType = null
            )
        }
        setEffect {
            ProfileContract.Effect.BottomSheet.Hide
        }
    }

    /**
     * This function is used when the user attempts to exit from the `Cropped Image` screen by
     * clicking the system's back button, the close button, or the `discard` button.
     */
    private fun exitCroppedImageScreen() {
        setState {
            copy(
                errorAlertDialog = ProfileErrorAlertDialog(
                    errorTitle = resourceProvider.getString(R.string.profile_screen_label_error_alert_dialog_cropped_image_title_exit),
                    errorSubtitle = resourceProvider.getString(R.string.profile_screen_label_error_alert_dialog_cropped_image_subtitle_exit),
                    alertDialogButtonNegative = AlertDialogButton(
                        label = resourceProvider.getString(R.string.profile_screen_label_error_alert_dialog_cropped_image_button_negative),
                        onClick = {
                            setState {
                                copy(
                                    croppedImageFileContentData = null,
                                    errorAlertDialog = null
                                )
                            }
                        }
                    ),
                    alertDialogButtonPositive = AlertDialogButton(
                        label = resourceProvider.getString(R.string.profile_screen_label_error_alert_dialog_cropped_image_button_positive),
                        buttonType = ErrorButtonType.Contained,
                        onClick = {
                            setState {
                                copy(
                                    errorAlertDialog = null
                                )
                            }
                        }
                    )
                )
            )
        }
    }

    private fun onSaveButtonClicked() {
        when (val fileContentData = currentState.croppedImageFileContentData) {
            null -> {
                setState {
                    copy(
                        croppedImageIsLoading = false,
                        croppedImageFileContentData = null
                    )
                }
            }

            else -> {
                setState {
                    copy(
                        croppedImageIsLoading = true
                    )
                }

                updateProfileImage(fileContentData)
            }
        }
    }

    /**
     * Updates the current profile image with the one that the user has cropped.
     */
    private fun updateProfileImage(newProfileImageData: AttachedFileContentData) {
        viewModelScope.launch {
            val result =
                userInteractor.updateUserProfileImage(profileImageData = newProfileImageData)
            when (result) {
                is MultipartResult.GenericError,
                is MultipartResult.NetworkError -> {
                    showErroneousSnackbar(errorMessage = resourceProvider.getString(R.string.screen_profile_error_image_upload))
                    setState {
                        copy(
                            croppedImageIsLoading = false,
                            croppedImageFileContentData = null
                        )
                    }
                }

                MultipartResult.ImageProcessingError -> {
                    showErroneousSnackbar(errorMessage = resourceProvider.getString(R.string.screen_profile_error_image_crop_reading))
                    setState {
                        copy(
                            croppedImageIsLoading = false,
                            croppedImageFileContentData = null
                        )
                    }
                }

                is MultipartResult.Success -> {
                    showSuccessSnackbar(
                        message = resourceProvider.getString(R.string.screen_profile_success_image_upload_message),
                        snackbarActionData = SnackbarActionData(
                            label = resourceProvider.getString(R.string.screen_profile_success_image_upload_action_label),
                            actionType = SnackbarActionType.Hide
                        )
                    )
                    setState {
                        copy(
                            croppedImageIsLoading = false,
                            croppedImageFileContentData = null,
                            profileDetails = profileDetails?.copy(
                                profileImageUrlData = profileDetails.profileImageUrlData.copy(
                                    uuid = UUID.randomUUID()
                                )
                            )
                        )
                    }
                }
            }
        }
    }
}