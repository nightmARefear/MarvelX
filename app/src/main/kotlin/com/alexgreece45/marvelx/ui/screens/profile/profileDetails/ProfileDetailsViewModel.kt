package com.alexgreece45.marvelx.ui.screens.profile.profileDetails

import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.BuildConfig
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.domain.helper.InteractorPartialState
import com.alexgreece45.marvelx.domain.interactors.GetProfileDetailsPartialState
import com.alexgreece45.marvelx.domain.interactors.UserInteractor
import com.alexgreece45.marvelx.domain.model_api.user.profileDetails.ProfileDetailsDomain
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.components.AlertDialogButton
import com.alexgreece45.marvelx.ui.components.SnackbarState
import com.alexgreece45.marvelx.ui.components.model.ErrorButtonLayout
import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.model.ProfileDetailsAlertDialogErrorData
import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.model.ProfileDetailsBottomSheetType
import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.model.ProfileDetailsMoreActionItem
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.constants.ConstantsUi
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import kotlinx.coroutines.launch

sealed interface ProfileDetailsContract {
    /**
     * Error view data.
     */
    data class ErrorView(
        val errorTitle: String,
        val errorSubtitle: String,
        val onCloseClicked: Event,
        val errorButtonLayout: ErrorButtonLayout.Single
    )

    sealed interface Event : BaseEvent {
        data class Init(val userId: Int) : Event
        data object OnBackClicked : Event
        data object OnEditProfileDetailsIconClicked : Event

        sealed interface MoreActions : Event {
            data object OnClicked : MoreActions
            data object OnBlockClicked : MoreActions
            data object OnShareProfileClicked : MoreActions
            data object OnShowQrCodeClicked : MoreActions
        }

        sealed interface ProfileBanner : Event {
            data object OnFriendsListClicked : ProfileBanner
            data object OnListingsListClicked : ProfileBanner
            data object OnFavoritesListClicked : ProfileBanner
        }

        /**
         * Triggered when the user clicks on the "block" button.
         */
        data object OnBlockConfirmClicked : Event

        /**
         * Hides the alert dialog.
         */
        data object HideAlertDialog : Event
    }

    /**
     * @param isLoading                     Whether this screen is in loading state. It has the
     * default value of `true`.
     * @param profileDetails                The profile details of the user. It has the default
     * value of `null` indicating no user data.
     */
    data class State(
        // Loading.
        val isLoading: Boolean = true,

        // Modal bottom sheet data.
        val moreActionItems: List<ProfileDetailsMoreActionItem> = emptyList(),
        val bottomSheetType: ProfileDetailsBottomSheetType? = null,

        // Data.
        val isGenderDropDownExpanded: Boolean = false,
        val profileDetails: ProfileDetailsDomain? = null,
        val userId: Int? = null,
        val isDifferentUser: Boolean = true,
        val isGenderHidden: Boolean = false,
        val isEmailHidden: Boolean = false,

        // Errors.
        val snackbarState: SnackbarState? = null,
        val alertDialogError: ProfileDetailsAlertDialogErrorData? = null,
        val errorView: ErrorView? = null,
    ) : BaseState

    sealed interface Effect : BaseEffect {
        /**
         * Shows the bottom sheet.
         */
        data object HideBottomSheet : Effect

        /**
         * Shows the bottom sheet by firstly hiding the current one. If no bottom sheet is visible,
         * this [Effect] won't cause any issues. [setState] is executed after the hiding of the
         * current bottom sheet, and it actually sets the ViewModel state for the new bottom sheet
         * type. After [setState] is executed, the new bottom sheet is shown.
         */
        data class ShowBottomSheet(val setState: () -> Unit) : Effect

        /**
         * Executes events.
         */
        data class ExecuteEvent(val event: Event) : Effect

        sealed interface Navigation : Effect {
            data object PopBackStack : Navigation
            data object NavigateToEditProfileDetailsScreen : Navigation
            data object NavigateToFriendsScreen : Navigation
            data object NavigateToListingsScreen : Navigation
            data object NavigateToFavoritesScreen : Navigation
        }
    }
}

class ProfileDetailsViewModel(
    private val userInteractor: UserInteractor,
    private val resourceProvider: ResourceProvider,
) : BaseViewModel<ProfileDetailsContract.Event, ProfileDetailsContract.State, ProfileDetailsContract.Effect>() {
    override fun setInitialState(): ProfileDetailsContract.State =
        ProfileDetailsContract.State()

    override fun handleEvents(event: ProfileDetailsContract.Event) {
        when (event) {
            is ProfileDetailsContract.Event.Init -> {
                initializeMoreActionItems()
                loadProfileDetails(event)
            }

            ProfileDetailsContract.Event.OnBackClicked -> {
                setEffect {
                    ProfileDetailsContract.Effect.Navigation.PopBackStack
                }
            }

            ProfileDetailsContract.Event.OnEditProfileDetailsIconClicked -> {
                setEffect {
                    ProfileDetailsContract.Effect.Navigation.NavigateToEditProfileDetailsScreen
                }
            }

            ProfileDetailsContract.Event.MoreActions.OnClicked -> {
                showBottomSheet(bottomSheetType = ProfileDetailsBottomSheetType.MoreActions)
            }

            ProfileDetailsContract.Event.MoreActions.OnBlockClicked -> {
                hideBottomSheet()
                showAlertDialogError(
                    profileDetailsAlertDialogErrorData = ProfileDetailsAlertDialogErrorData(
                        errorTitle = resourceProvider.getString(R.string.profile_details_screen_label_alert_dialog_block_user_title),
                        errorSubtitle = resourceProvider.getString(R.string.profile_details_screen_label_alert_dialog_block_user_subtitle),
                        dismissAlertDialogButton = AlertDialogButton(
                            value = resourceProvider.getString(R.string.profile_details_screen_label_alert_dialog_block_user_button_cancel),
                            onClick = {
                                setEffect {
                                    ProfileDetailsContract.Effect.ExecuteEvent(
                                        ProfileDetailsContract.Event.HideAlertDialog
                                    )
                                }
                            }
                        ),
                        confirmAlertDialogButton = AlertDialogButton(
                            value = resourceProvider.getString(R.string.profile_details_screen_label_alert_dialog_block_user_button_block),
                            onClick = {
                                setEffect {
                                    ProfileDetailsContract.Effect.ExecuteEvent(
                                        ProfileDetailsContract.Event.HideAlertDialog
                                    )
                                }
                                setEffect {
                                    ProfileDetailsContract.Effect.ExecuteEvent(
                                        ProfileDetailsContract.Event.OnBlockConfirmClicked
                                    )
                                }
                            }
                        )
                    )
                )
            }

            ProfileDetailsContract.Event.MoreActions.OnShareProfileClicked -> {
                // Send deep-link url. Open with intent
            }

            ProfileDetailsContract.Event.MoreActions.OnShowQrCodeClicked -> {
                val userId = currentState.userId
                if (userId == null) {
                    setEffect {
                        ProfileDetailsContract.Effect.Navigation.PopBackStack
                    }
                    showGenericAlertDialogError(resourceProvider.getString(R.string.profile_details_screen_error_generating_qr_code))
                    return
                }

                when (val qrBitmap =
                    generateQRCodeBitmap(BuildConfig.DEEPLINK_PROFILE_DETAILS + userId.toString())) {
                    null -> {
                        showGenericAlertDialogError(resourceProvider.getString(R.string.profile_details_screen_error_generating_qr_code))
                    }

                    else -> {
                        showBottomSheet(
                            bottomSheetType = ProfileDetailsBottomSheetType.QrCode(
                                qrBitmap = qrBitmap
                            )
                        )
                    }
                }
            }

            ProfileDetailsContract.Event.ProfileBanner.OnFriendsListClicked -> {
                if (currentState.profileDetails?.privacySettings?.isFriendsListVisible == true) {
                    setEffect {
                        ProfileDetailsContract.Effect.Navigation.NavigateToFriendsScreen
                    }
                } else {
                    showGenericAlertDialogError(resourceProvider.getString(R.string.profile_details_screen_error_hidden_list_friends))
                }
            }

            ProfileDetailsContract.Event.ProfileBanner.OnListingsListClicked -> {
                if (currentState.profileDetails?.privacySettings?.isListingsListVisible == true) {
                    setEffect {
                        ProfileDetailsContract.Effect.Navigation.NavigateToListingsScreen
                    }
                } else {
                    showGenericAlertDialogError(resourceProvider.getString(R.string.profile_details_screen_error_hidden_list_listings))
                }
            }

            ProfileDetailsContract.Event.ProfileBanner.OnFavoritesListClicked -> {
                if (currentState.profileDetails?.privacySettings?.isFavoritesListVisible == true) {
                    setEffect {
                        ProfileDetailsContract.Effect.Navigation.NavigateToFavoritesScreen
                    }
                } else {
                    showGenericAlertDialogError(resourceProvider.getString(R.string.profile_details_screen_error_hidden_list_favorites))
                }
            }

            ProfileDetailsContract.Event.HideAlertDialog -> {
                hideAlertDialogError()
            }

            ProfileDetailsContract.Event.OnBlockConfirmClicked -> {
                setState {
                    copy(
                        isLoading = true
                    )
                }
                viewModelScope.launch {
                    userInteractor.blockUser(currentState.userId ?: -1).collect {
                        when (it) {
                            is InteractorPartialState.Success -> {
                                showSnackbar(message = resourceProvider.getString(R.string.profile_details_screen_label_success_block_user))
                                setEffect {
                                    ProfileDetailsContract.Effect.Navigation.PopBackStack
                                }
                            }

                            is InteractorPartialState.Error, is InteractorPartialState.Failed, InteractorPartialState.NoInternetConnection -> {
                                setState {
                                    copy(
                                        isLoading = false
                                    )
                                }
                                showSnackbar(message = resourceProvider.getString(R.string.profile_details_screen_error_blocking_user))
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Initializes the more actions item bottom sheet only once.
     */
    private fun initializeMoreActionItems() {
        if (currentState.moreActionItems.isNotEmpty()) return
        setState {
            copy(
                moreActionItems = listOf(
                    ProfileDetailsMoreActionItem(
                        label = resourceProvider.getString(R.string.profile_details_screen_label_more_actions_block),
                        isRestrictiveAction = true,
                        event = ProfileDetailsContract.Event.MoreActions.OnBlockClicked
                    ),
                    ProfileDetailsMoreActionItem(
                        label = resourceProvider.getString(R.string.profile_details_screen_label_more_actions_share_profile),
                        event = ProfileDetailsContract.Event.MoreActions.OnShareProfileClicked
                    ),
                    ProfileDetailsMoreActionItem(
                        label = resourceProvider.getString(R.string.profile_details_screen_label_more_actions_show_qr_code),
                        event = ProfileDetailsContract.Event.MoreActions.OnShowQrCodeClicked
                    ),
                )
            )
        }
    }

    /**
     * Loads the profile details by user ID.
     */
    private fun loadProfileDetails(event: ProfileDetailsContract.Event.Init) {
        setState {
            copy(
                isLoading = true,
                errorView = null
            )
        }
        viewModelScope.launch {
            userInteractor.getProfileDetails(event.userId).collect {
                when (it) {
                    is GetProfileDetailsPartialState.Success -> {
                        setState {
                            copy(
                                isLoading = false,
                                errorView = null,
                                profileDetails = it.profileDetailsDomain,
                                userId = event.userId,
                                isDifferentUser = it.profileDetailsDomain.isDifferentUser,
                                isGenderHidden = it.profileDetailsDomain.isDifferentUser && it.profileDetailsDomain.privacySettings != null && !it.profileDetailsDomain.privacySettings.isGenderVisible,
                                isEmailHidden = it.profileDetailsDomain.isDifferentUser && it.profileDetailsDomain.privacySettings != null && !it.profileDetailsDomain.privacySettings.isEmailVisible,
                            )
                        }
                    }

                    is GetProfileDetailsPartialState.Failed -> {
                        setState {
                            copy(
                                isLoading = false,
                                errorView = ProfileDetailsContract.ErrorView(
                                    errorTitle = resourceProvider.getString(R.string.profile_details_screen_error_loading_profile_details),
                                    errorSubtitle = resourceProvider.getString(R.string.internal_server_error),
                                    onCloseClicked = ProfileDetailsContract.Event.OnBackClicked,
                                    errorButtonLayout = ErrorButtonLayout.Single(
                                        primaryButtonLabel = resourceProvider.getString(R.string.generic_retry_button),
                                        onPrimaryButtonClicked = { loadProfileDetails(event) }
                                    )
                                )
                            )
                        }
                    }

                    is GetProfileDetailsPartialState.NoInternetConnection -> {
                        setState {
                            copy(
                                isLoading = false,
                                errorView = ProfileDetailsContract.ErrorView(
                                    errorTitle = resourceProvider.getString(R.string.profile_details_screen_error_loading_profile_details),
                                    errorSubtitle = resourceProvider.getNoInternetConnectionMessage(),
                                    onCloseClicked = ProfileDetailsContract.Event.OnBackClicked,
                                    errorButtonLayout = ErrorButtonLayout.Single(
                                        primaryButtonLabel = resourceProvider.getString(R.string.generic_retry_button),
                                        onPrimaryButtonClicked = { loadProfileDetails(event) }
                                    )
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * Attempts to generate a QR code bitmap with the given [content]. If the QR generation failed,
     * then `null` is returned.
     *
     * @param content   The content of the QR.
     */
    private fun generateQRCodeBitmap(content: String): Bitmap? {
        val width = ConstantsUi.ProfileDetailsScreen.QR_CODE_SIZE
        val height = ConstantsUi.ProfileDetailsScreen.QR_CODE_SIZE
        return try {
            val bitMatrix =
                MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height)
            val w = bitMatrix.width
            val h = bitMatrix.height
            val pixels = IntArray(w * h)
            for (y in 0 until h) {
                val offset = y * w
                for (x in 0 until w) {
                    pixels[offset + x] =
                        if (bitMatrix[x, y]) 0xFF000000.toInt() else 0xFFFFFFFF.toInt()
                }
            }
            Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888).apply {
                setPixels(pixels, 0, width, 0, 0, width, height)
            }
        } catch (e: WriterException) {
            appLogger.e("ProfileDetailsViewModel") { "(generateQRCodeBitmap) e: $e" }
            null
        }
    }

    /**
     * Shows the bottom sheet of type [bottomSheetType].
     *
     * __Note:__ It first hides the current bottom sheet.
     */
    private fun showBottomSheet(bottomSheetType: ProfileDetailsBottomSheetType) {
        // Just in case there is a bottom sheet type.
        currentState.bottomSheetType?.let {
            setState {
                copy(
                    bottomSheetType = null
                )
            }
        }
        setEffect {
            ProfileDetailsContract.Effect.ShowBottomSheet {
                setState {
                    copy(
                        bottomSheetType = bottomSheetType
                    )
                }
            }
        }
    }

    /**
     * Hides the bottom sheet.
     */
    private fun hideBottomSheet() {
        currentState.bottomSheetType?.let {
            setState {
                copy(
                    bottomSheetType = null
                )
            }
        }
        setEffect {
            ProfileDetailsContract.Effect.HideBottomSheet
        }
    }

    /**
     * Shows the alert dialog error with the given [ProfileDetailsAlertDialogErrorData].
     */
    private fun showAlertDialogError(profileDetailsAlertDialogErrorData: ProfileDetailsAlertDialogErrorData) {
        setState {
            copy(
                alertDialogError = profileDetailsAlertDialogErrorData
            )
        }
    }

    /**
     * Shows a generic alert dialog error that has a single button of value "OK", that when clicked,
     * the dialog closes. The title of the error is [R.string.error], while the subtitle is
     * configurable via [errorSubtitle].
     */
    private fun showGenericAlertDialogError(errorSubtitle: String) {
        setState {
            copy(
                alertDialogError = ProfileDetailsAlertDialogErrorData(
                    errorTitle = resourceProvider.getString(R.string.error),
                    errorSubtitle = errorSubtitle,
                    confirmAlertDialogButton = AlertDialogButton(
                        value = resourceProvider.getString(R.string.generic_ok_button),
                        onClick = {
                            setEffect {
                                ProfileDetailsContract.Effect.ExecuteEvent(
                                    event = ProfileDetailsContract.Event.HideAlertDialog
                                )
                            }
                        }
                    )
                )
            )
        }
    }

    /**
     * Hides the alert dialog error.
     */
    private fun hideAlertDialogError() {
        setState {
            copy(
                alertDialogError = null
            )
        }
    }

    /**
     * Shows the snackbar with the given [message].
     */
    private fun showSnackbar(message: String) {
        setState {
            copy(
                snackbarState = SnackbarState(message)
            )
        }
    }
}
