package com.alexgreece45.marvelx.ui.screens.profile

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.configuration.ConfigLogic
import com.alexgreece45.marvelx.controllers.PrefKeys
import com.alexgreece45.marvelx.domain.helper.InteractorPartialState
import com.alexgreece45.marvelx.domain.interactors.GetCurrentUserProfileDetailsPartialState
import com.alexgreece45.marvelx.domain.interactors.UserInteractor
import com.alexgreece45.marvelx.domain.model_api.user.profileDetails.BasicProfileDetailsDomain
import com.alexgreece45.marvelx.domain.model_generic.AttachedFileContentData
import com.alexgreece45.marvelx.domain.model_generic.ReadFileContentData
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.components.AlertDialogButton
import com.alexgreece45.marvelx.ui.components.IconData
import com.alexgreece45.marvelx.ui.components.model.BottomSheetContentItem
import com.alexgreece45.marvelx.ui.components.model.BottomSheetContentItemIconData
import com.alexgreece45.marvelx.ui.components.model.ErrorButtonType
import com.alexgreece45.marvelx.ui.components.sections.model.SectionHeaderData
import com.alexgreece45.marvelx.ui.helper.model.DeepLinkAction
import com.alexgreece45.marvelx.ui.navigation.Screen
import com.alexgreece45.marvelx.ui.screens.profile.model.ProfileBottomSheetType
import com.alexgreece45.marvelx.ui.screens.profile.model.ProfileErrorAlertDialog
import com.alexgreece45.marvelx.ui.screens.profile.model.ProfileScreenErrorView
import com.alexgreece45.marvelx.util.FileUtil
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.constants.ConstantsUi
import com.alexgreece45.marvelx.util.extensions.endsWithAnySuffix
import com.alexgreece45.marvelx.util.extensions.toUrlEncoding
import kotlinx.coroutines.launch

sealed interface ProfileContract {
    sealed interface Event : BaseEvent {
        data class Init(val deepLinkAction: DeepLinkAction?) : Event
        data object OnSettingsIconButtonClicked : Event
        data object OnSystemBack : Event
        data object OnSeeYourProfileClicked : Event
        data object OnErrorClose : Event

        /**
         * All the events related to the profile image.
         */
        sealed interface ProfileImage : Event {
            /**
             * Triggered when the profile image has loaded.
             */
            data object OnLoaded : ProfileImage

            /**
             * Triggered when the profile image is clicked.
             */
            data object OnClicked : ProfileImage

            /**
             * Triggered when an image is selected in the `photo picker`.
             */
            data class OnImageSelected(val imageFileContentData: AttachedFileContentData?) :
                ProfileImage

            /**
             * Triggered when a selected image has been cropped.
             */
            data class OnImageCropped(val imageFileContentData: AttachedFileContentData?) :
                ProfileImage

            /**
             * Triggered when the cropped image's content is read.
             */
            data class OnCroppedImageFileContentRead(val croppedImageFileReadContentData: ReadFileContentData?) :
                ProfileImage

            /**
             * Events for updating the current profile image.
             */
            sealed interface CroppedImage : ProfileImage {
                /**
                 * Triggered when the user clicks on the `close` nav icon button.
                 */
                data object OnCloseClicked : CroppedImage

                /**
                 * Triggered when the user clicks on the `save` button.
                 */
                data object OnSaveClicked : CroppedImage
            }

            /**
             * Profile image bottom sheet events.
             */
            sealed interface BottomSheet : ProfileImage {

                /**
                 * Triggered when the `view image` item is clicked.
                 */
                data object OnViewImageClicked : BottomSheet

                /**
                 * Triggered when the `upload image` item is clicked.
                 */
                data object OnUploadImageClicked : BottomSheet
            }
        }
    }

    /**
     * @param isLoading Whether the screen is in loading state.
     * @param croppedImageIsLoading Whether the `Cropped Image` screen is in loading state.
     * @param croppedImageFileContentData The file content data of the cropped image.
     * When it's not `null`, a full screen view is displayed.
     */
    data class State(
        val isLoading: Boolean = true,
        val croppedImageIsLoading: Boolean = false,

        val profileBannerLabel: String = "",
        val sections: List<SectionHeaderData> = emptyList(),
        val profileDetails: BasicProfileDetailsDomain? = null,
        val croppedImageFileContentData: AttachedFileContentData? = null,

        val bottomSheetType: ProfileBottomSheetType? = null,
        val profileImageBottomSheetContentItems: List<BottomSheetContentItem> = emptyList(),

        val error: ProfileScreenErrorView? = null,
        val errorAlertDialog: ProfileErrorAlertDialog? = null,
    ) : BaseState

    sealed class Effect : BaseEffect {
        sealed class BottomSheet : Effect() {
            data object Show : BottomSheet()
            data object Hide : BottomSheet()
        }

        sealed class Navigation : Effect() {
            data object NavigateUp : Navigation()
            data object NavigateToSettingsScreen : Navigation()
            data object NavigateToFriendsScreen : Navigation()
            data class NavigateToQrCodeScreen(val previousScreenRoute: String) : Navigation()
            data class NavigateToProfileImageScreen(val profileImageUrl: String) : Navigation()
            data class NavigateToProfileDetailsScreen(val userId: String) : Navigation()
        }

        /**
         * Opens the photo picker.
         */
        data object OpenPhotoPicker : Effect()

        /**
         * Opens the image cropper with the given [imageUri].
         */
        data class OpenImageCropper(
            val toolbarTitle: String,
            val imageSize: Int,
            val imageUri: Uri
        ) : Effect()

        data class ReadCroppedImageFileContent(
            val fileUri: Uri,
            val fileName: String
        ) : Effect()

        data class OpenDeepLinkAction(val deepLinkAction: DeepLinkAction) : Effect()

        data class ShowToastMessage(val message: String) : Effect()
    }
}


class ProfileViewModel(
    private val resourceProvider: ResourceProvider,
    private val userInteractor: UserInteractor,
    private val prefKeys: PrefKeys,
    private val configLogic: ConfigLogic
) :
    BaseViewModel<ProfileContract.Event, ProfileContract.State, ProfileContract.Effect>() {
    override fun setInitialState(): ProfileContract.State = ProfileContract.State()

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
                setEffect {
                    ProfileContract.Effect.Navigation.NavigateToProfileDetailsScreen(prefKeys.getUserId())
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
                        currentState.profileDetails?.profileImageUrl.orEmpty().toUrlEncoding()
                    )
                }
            }

            is ProfileContract.Event.ProfileImage.OnImageSelected -> {
                val imageFileContentData = event.imageFileContentData
                if (imageFileContentData == null) {
                    showToastMessage(message = resourceProvider.getString(R.string.screen_profile_error_image_select_reading))
                    return
                }

                if (!imageFileContentData.name.endsWithAnySuffix(suffixes = configLogic.supportedImageFileExtensions)) {
                    showToastMessage(message = resourceProvider.getString(R.string.screen_profile_error_image_select_invalid_extension))
                    return
                }

                if (imageFileContentData.bytesCount > configLogic.imageFileMaxSize) {
                    showToastMessage(
                        message = resourceProvider.getString(
                            R.string.screen_profile_error_image_select_too_big,
                            FileUtil.getMegabytesFromBytes(configLogic.imageFileMaxSize)
                        )
                    )
                    return
                }

                appLogger.d("ProfileViewModel") { "(OnImageSelected) selected image URI: ${imageFileContentData.uri}" }
                setEffect {
                    ProfileContract.Effect.OpenImageCropper(
                        toolbarTitle = resourceProvider.getString(R.string.profile_screen_cropped_cropped_image_toolbar_title),
                        imageSize = ConstantsUi.PROFILE_IMAGE_FULL_SIZE.value.toInt(),
                        imageUri = imageFileContentData.uri
                    )
                }
            }

            is ProfileContract.Event.ProfileImage.OnImageCropped -> {
                if (event.imageFileContentData == null) {
                    appLogger.e("ProfileViewModel") { "Error cropping image!" }

                    showToastMessage(message = resourceProvider.getString(R.string.screen_profile_error_image_crop))
                    setState {
                        copy(
                            croppedImageIsLoading = false,
                            croppedImageFileContentData = null
                        )
                    }
                    return
                }

                appLogger.d("ProfileViewModel") { "Image was successfully cropped!" }
                setState {
                    copy(
                        croppedImageFileContentData = event.imageFileContentData
                    )
                }
            }

            is ProfileContract.Event.ProfileImage.OnCroppedImageFileContentRead -> {
                val readFileContentData = event.croppedImageFileReadContentData
                if (readFileContentData == null) {
                    showToastMessage(message = resourceProvider.getString(R.string.screen_profile_error_image_crop_reading))
                    setState {
                        copy(
                            croppedImageIsLoading = false,
                            croppedImageFileContentData = null
                        )
                    }
                    return
                }

                updateProfileImage(
                    readFileContentData = readFileContentData
                )
            }

            ProfileContract.Event.ProfileImage.CroppedImage.OnCloseClicked -> {
                exitCroppedImageScreen()
            }

            ProfileContract.Event.ProfileImage.CroppedImage.OnSaveClicked -> {
                readCroppedImageFileContentData()
            }
        }
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
                                ProfileContract.Effect.Navigation.NavigateToQrCodeScreen(Screen.ProfileScreen.baseRoute)
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
                    )
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
                        onClick = ProfileContract.Event.ProfileImage.BottomSheet.OnViewImageClicked
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
                        onClick = ProfileContract.Event.ProfileImage.BottomSheet.OnUploadImageClicked
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

    private fun readCroppedImageFileContentData() {
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

                setEffect {
                    ProfileContract.Effect.ReadCroppedImageFileContent(
                        fileUri = fileContentData.uri,
                        fileName = fileContentData.name
                    )
                }
            }
        }
    }

    /**
     * Updates the current profile image with the one that the user has cropped.
     */
    private fun updateProfileImage(readFileContentData: ReadFileContentData) {
        viewModelScope.launch {
            userInteractor.updateUserProfileImage(profileImageReadFileContentData = readFileContentData)
                .collect { result ->
                    when (result) {
                        is InteractorPartialState.Error<*>,
                        is InteractorPartialState.Failed<*>,
                        InteractorPartialState.NoInternetConnection -> {
                            showToastMessage(message = resourceProvider.getString(R.string.screen_profile_error_image_upload))
                            setState {
                                copy(
                                    croppedImageIsLoading = false,
                                    croppedImageFileContentData = null
                                )
                            }
                        }

                        is InteractorPartialState.Success<*> -> {
                            showToastMessage(message = resourceProvider.getString(R.string.screen_profile_success_image_upload))
                            setState {
                                copy(
                                    croppedImageIsLoading = false,
                                    croppedImageFileContentData = null
                                )
                            }
                        }
                    }
                }
        }
    }

    private fun showToastMessage(message: String) {
        setEffect {
            ProfileContract.Effect.ShowToastMessage(message = message)
        }
    }
}
