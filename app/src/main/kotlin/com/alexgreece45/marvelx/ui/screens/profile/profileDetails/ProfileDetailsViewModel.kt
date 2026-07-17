package com.alexgreece45.marvelx.ui.screens.profile.profileDetails

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.createBitmap
import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarStateData
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarType
import com.alexgreece45.marvelx.configuration.models.DeepLinkWebUrl
import com.alexgreece45.marvelx.core.common.dispatchers.model.IoDispatcher
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import com.alexgreece45.marvelx.data.repository.NotificationRepository
import com.alexgreece45.marvelx.data.repository.user.FriendRepository
import com.alexgreece45.marvelx.domain.interactors.GetProfileDetailsPartialState
import com.alexgreece45.marvelx.domain.interactors.UserInteractor
import com.alexgreece45.marvelx.domain.interactors.user.BlockUserPartialState
import com.alexgreece45.marvelx.domain.interactors.user.BlockedUserInteractor
import com.alexgreece45.marvelx.domain.interactors.user.FriendRequestInteractor
import com.alexgreece45.marvelx.domain.model_api.user.block.UserBlockResultType
import com.alexgreece45.marvelx.domain.model_api.user.friendRequest.DeleteFriendRequestType
import com.alexgreece45.marvelx.domain.model_api.user.profileDetails.ProfileDetailsDomain
import com.alexgreece45.marvelx.domain.model_api.user.profileDetails.ProfileDetailsErrorType
import com.alexgreece45.marvelx.domain.model_api.user.profileDetails.RelationshipStatusDomain
import com.alexgreece45.marvelx.model.model.user.UserId
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.components.AlertDialogButton
import com.alexgreece45.marvelx.ui.components.model.BottomSheetContentItem
import com.alexgreece45.marvelx.ui.components.model.ErrorButtonLayout
import com.alexgreece45.marvelx.ui.components.model.ErrorButtonType
import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.model.ProfileDetailsAlertDialogErrorData
import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.model.ProfileDetailsBottomSheetType
import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.model.ProfileDetailsFullScreenErrorView
import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.model.ProfileDetailsNestedErrorView
import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.stats.model.ProfileDetailsStatsTab
import com.alexgreece45.marvelx.ui_theme.theme.values.getErrorColor
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.constants.ConstantsUi
import com.alexgreece45.marvelx.util.extensions.toUrlEncoding
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.annotation.KoinViewModel
import java.util.UUID
import kotlin.coroutines.CoroutineContext

sealed interface ProfileDetailsContract {
    sealed interface Event : BaseEvent {
        data class Init(val userId: String, val shouldShowAcceptFriendRequestDialog: Boolean) :
            Event

        data object OnBackClicked : Event
        data object OnEditProfileDetailsIconClicked : Event

        sealed interface MoreActions : Event {
            data object OnClicked : MoreActions
            data object OnBlockClicked : MoreActions
            data object OnShareProfileClicked : MoreActions
            data object OnShowQrCodeClicked : MoreActions
        }

        sealed interface ProfileBanner : Event {
            data object OnProfileImageClicked : ProfileBanner
            data object OnFriendsListClicked : ProfileBanner
            data object OnListingsListClicked : ProfileBanner
            data object OnFavoritesListClicked : ProfileBanner

            data object OnGenderLongClick : ProfileBanner
            data object OnEmailLongClick : ProfileBanner

            data object OnRelationshipStatusChipClicked : ProfileBanner
        }

        /**
         * Hides the alert dialog.
         */
        data object HideAlertDialog : Event

        /**
         * Triggered when the system's back button is clicked.
         */
        data object OnSystemBack : Event
    }

    /**
     * @param isLoading Whether this screen is in loading state. It has the default value of `true`.
     * @param isRelationshipStatusChipLoading Whether the `relationship status` chip is in loading
     * state.
     * @param profileDetails The profile details of the user. It has the default value of `null`
     * indicating no user data.
     */
    data class State(
        // Loading.
        val isLoading: Boolean = true,
        val isRelationshipStatusChipLoading: Boolean = false,

        // Modal bottom sheet data.
        val moreActionItems: List<BottomSheetContentItem> = emptyList(),
        val bottomSheetType: ProfileDetailsBottomSheetType? = null,

        // Data.
        val isGenderDropDownExpanded: Boolean = false,
        val profileDetails: ProfileDetailsDomain? = null,
        val userId: UserId = UserId.EMPTY,
        val isGenderHidden: Boolean = false,
        val isEmailHidden: Boolean = false,

        // Errors.
        val snackbarStateData: SnackbarStateData? = null,
        val alertDialogError: ProfileDetailsAlertDialogErrorData? = null,
        val fullScreenErrorView: ProfileDetailsFullScreenErrorView? = null,

        /**
         * The nested error view inside the bounds of the scaffold (under the toolbar).
         */
        val nestedErrorView: ProfileDetailsNestedErrorView? = null,
    ) : BaseState

    sealed interface Effect : BaseEffect {
        /**
         * Shows the bottom sheet. Make sure to firstly set its type in the ViewModel state.
         */
        data object ShowBottomSheet : Effect

        /**
         * Hides the bottom sheet.
         */
        data object HideBottomSheet : Effect

        data class ShowSnackbar(val snackbarStateData: SnackbarStateData) : Effect
        data object HideSnackbar : Effect

        data class ShareProfileUrl(val url: String, val intentTitle: String) : Effect

        data class SetClipboardText(val toastMessage: String, val text: String) : Effect

        sealed interface Navigation : Effect {
            data object PopBackStack : Navigation
            data class NavigateToProfileImageScreen(val profileImageUrl: String) : Navigation
            data object NavigateToEditProfileDetailsScreen : Navigation

            /**
             * Navigates the user to the `ProfileDetailsStats` screen.
             */
            data class NavigateToProfileDetailsStatsScreen(
                val fullName: String,
                val userId: String,
                val tab: ProfileDetailsStatsTab
            ) : Navigation
        }
    }
}

@KoinViewModel
class ProfileDetailsViewModel(
    private val userInteractor: UserInteractor,
    private val resourceProvider: ResourceProvider,
    private val friendRepository: FriendRepository,
    private val friendRequestInteractor: FriendRequestInteractor,
    private val blockedUserInteractor: BlockedUserInteractor,
    private val notificationRepository: NotificationRepository,
    private val appLogger: AppLogger,
    @IoDispatcher private val ioDispatcher: CoroutineContext,
) : BaseViewModel<ProfileDetailsContract.Event, ProfileDetailsContract.State, ProfileDetailsContract.Effect>() {

    override fun setInitialState(): ProfileDetailsContract.State =
        ProfileDetailsContract.State()

    override fun handleEvents(event: ProfileDetailsContract.Event) {
        when (event) {
            is ProfileDetailsContract.Event.Init -> {
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
                            label = resourceProvider.getString(R.string.profile_details_screen_label_alert_dialog_block_user_button_cancel),
                            onClick = ::hideAlertDialogError
                        ),
                        confirmAlertDialogButton = AlertDialogButton(
                            label = resourceProvider.getString(R.string.profile_details_screen_label_alert_dialog_block_user_button_block),
                            buttonType = ErrorButtonType.Contained,
                            onClick = ::onBlockConfirmClicked
                        )
                    )
                )
            }

            ProfileDetailsContract.Event.MoreActions.OnShareProfileClicked -> {
                hideBottomSheet()
                setEffect {
                    ProfileDetailsContract.Effect.ShareProfileUrl(
                        url = DeepLinkWebUrl.ProfileDetails.webUrl + currentState.userId,
                        intentTitle = resourceProvider.getString(
                            resId = R.string.profile_details_screen_label_share_profile_intent_title,
                            formatArgs = arrayOf(currentState.profileDetails?.basicProfileDetails?.fullName.orEmpty())
                        )
                    )
                }
            }

            ProfileDetailsContract.Event.MoreActions.OnShowQrCodeClicked -> {
                viewModelScope.launch {
                    val qrBitmap =
                        withContext(ioDispatcher) { generateQRCodeBitmap(DeepLinkWebUrl.ProfileDetails.webUrl + currentState.userId) }
                    when (qrBitmap) {
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
            }

            ProfileDetailsContract.Event.ProfileBanner.OnProfileImageClicked -> {
                setEffect {
                    ProfileDetailsContract.Effect.Navigation.NavigateToProfileImageScreen(
                        profileImageUrl = currentState.profileDetails?.basicProfileDetails?.profileImageUrlData?.imageUrl.orEmpty()
                            .toUrlEncoding()
                    )
                }
            }

            ProfileDetailsContract.Event.ProfileBanner.OnFriendsListClicked -> {
                if (currentState.profileDetails?.isDifferentUser == false
                    || currentState.profileDetails?.privacySettings?.isFriendsListVisible == true
                ) {
                    navigateToProfileDetailsStatsScreen(tab = ProfileDetailsStatsTab.Friends)
                } else {
                    showGenericAlertDialogError(resourceProvider.getString(R.string.profile_details_screen_error_hidden_list_friends))
                }
            }

            ProfileDetailsContract.Event.ProfileBanner.OnListingsListClicked -> {
                if (currentState.profileDetails?.isDifferentUser == false
                    || currentState.profileDetails?.privacySettings?.isListingsListVisible == true
                ) {
                    navigateToProfileDetailsStatsScreen(tab = ProfileDetailsStatsTab.Listings)
                    return
                } else {
                    showGenericAlertDialogError(resourceProvider.getString(R.string.profile_details_screen_error_hidden_list_listings))
                }
            }

            ProfileDetailsContract.Event.ProfileBanner.OnFavoritesListClicked -> {
                if (currentState.profileDetails?.isDifferentUser == false
                    || currentState.profileDetails?.privacySettings?.isFavoritesListVisible == true
                ) {
                    navigateToProfileDetailsStatsScreen(tab = ProfileDetailsStatsTab.Favorites)
                } else {
                    showGenericAlertDialogError(resourceProvider.getString(R.string.profile_details_screen_error_hidden_list_favorites))
                }
            }

            ProfileDetailsContract.Event.ProfileBanner.OnGenderLongClick -> {
                currentState.profileDetails?.let { profileDetails ->
                    setEffect {
                        ProfileDetailsContract.Effect.SetClipboardText(
                            toastMessage = resourceProvider.getString(
                                R.string.generic_successfully_copied,
                                resourceProvider.getString(R.string.profile_details_screen_label_gender_field)
                            ),
                            text = resourceProvider.getString(profileDetails.basicProfileDetails.gender.label)
                        )
                    }
                }
            }

            ProfileDetailsContract.Event.ProfileBanner.OnEmailLongClick -> {
                currentState.profileDetails?.let { profileDetails ->
                    setEffect {
                        ProfileDetailsContract.Effect.SetClipboardText(
                            toastMessage = resourceProvider.getString(
                                R.string.generic_successfully_copied,
                                resourceProvider.getString(R.string.profile_details_screen_label_email_field)
                            ),
                            text = profileDetails.basicProfileDetails.email
                        )
                    }
                }
            }

            ProfileDetailsContract.Event.ProfileBanner.OnRelationshipStatusChipClicked -> {
                currentState.profileDetails?.let { profileDetails ->
                    viewModelScope.launch {
                        when (profileDetails.relationshipStatus) {
                            RelationshipStatusDomain.None -> {
                                setState {
                                    copy(
                                        isRelationshipStatusChipLoading = true
                                    )
                                }

                                friendRequestInteractor.sendFriendRequest(currentState.userId)
                                    .collect { wasSent ->
                                        setState {
                                            copy(
                                                isRelationshipStatusChipLoading = false,
                                                profileDetails = if (wasSent) profileDetails.copy(
                                                    relationshipStatus = RelationshipStatusDomain.PendingFriendRequestSender
                                                ) else profileDetails
                                            )
                                        }

                                        if (!wasSent) {
                                            showErroneousSnackbar(resourceProvider.getString(R.string.profile_details_screen_error_sending_friend_request))
                                        }
                                    }
                            }

                            RelationshipStatusDomain.PendingFriendRequestSender -> {
                                showAlertDialogError(
                                    ProfileDetailsAlertDialogErrorData(
                                        errorTitle = resourceProvider.getString(R.string.profile_details_screen_label_alert_dialog_title_delete_friend_request),
                                        errorSubtitle = resourceProvider.getString(R.string.profile_details_screen_label_alert_dialog_subtitle_delete_friend_request),
                                        dismissAlertDialogButton = AlertDialogButton(
                                            label = resourceProvider.getString(R.string.profile_details_screen_label_alert_dialog_button_negative_delete_friend_request),
                                            onClick = ::hideAlertDialogError
                                        ),
                                        confirmAlertDialogButton = AlertDialogButton(
                                            label = resourceProvider.getString(R.string.profile_details_screen_label_alert_dialog_button_positive_delete_friend_request),
                                            buttonType = ErrorButtonType.Contained,
                                            onClick = ::deletePendingFriendRequest
                                        )
                                    )
                                )
                            }

                            RelationshipStatusDomain.PendingFriendRequestReceiver -> {
                                showAcceptFriendRequestDialog()
                            }

                            RelationshipStatusDomain.Friends -> {
                                showAlertDialogError(
                                    ProfileDetailsAlertDialogErrorData(
                                        errorTitle = resourceProvider.getString(R.string.profile_details_screen_label_alert_dialog_title_delete_friend),
                                        errorSubtitle = resourceProvider.getString(R.string.profile_details_screen_label_alert_dialog_subtitle_delete_friend),
                                        dismissAlertDialogButton = AlertDialogButton(
                                            label = resourceProvider.getString(R.string.profile_details_screen_label_alert_dialog_button_negative_delete_friend),
                                            onClick = ::hideAlertDialogError
                                        ),
                                        confirmAlertDialogButton = AlertDialogButton(
                                            label = resourceProvider.getString(R.string.profile_details_screen_label_alert_dialog_button_positive_delete_friend),
                                            buttonType = ErrorButtonType.Contained,
                                            onClick = ::deleteFriend
                                        )
                                    )
                                )
                            }

                            null -> {}
                        }
                    }
                }
            }

            ProfileDetailsContract.Event.HideAlertDialog -> {
                hideAlertDialogError()
            }

            ProfileDetailsContract.Event.OnSystemBack -> {
                when {
                    currentState.bottomSheetType != null -> {
                        hideBottomSheet()
                    }

                    else -> {
                        setEffect {
                            ProfileDetailsContract.Effect.Navigation.PopBackStack
                        }
                    }
                }
            }
        }
    }

    /**
     * Loads the profile details by user ID.
     */
    private fun loadProfileDetails(event: ProfileDetailsContract.Event.Init) {
        val userId = UserId(event.userId)

        setState {
            copy(
                isLoading = true,
                fullScreenErrorView = null
            )
        }
        viewModelScope.launch {
            userInteractor.getProfileDetails(userId).collect {
                when (it) {
                    is GetProfileDetailsPartialState.Success -> {
                        initializeMoreActionItems(it.profileDetailsDomain.isDifferentUser)
                        setState {
                            copy(
                                isLoading = false,
                                fullScreenErrorView = null,
                                profileDetails = it.profileDetailsDomain,
                                userId = userId,
                                isGenderHidden = it.profileDetailsDomain.isDifferentUser && it.profileDetailsDomain.privacySettings != null && !it.profileDetailsDomain.privacySettings.isGenderVisible,
                                isEmailHidden = it.profileDetailsDomain.isDifferentUser && it.profileDetailsDomain.privacySettings != null && !it.profileDetailsDomain.privacySettings.isEmailVisible,
                            )
                        }

                        if (event.shouldShowAcceptFriendRequestDialog) {
                            // Notify backend about notification opening.
                            viewModelScope.launch {
                                notificationRepository.markIncomingFriendRequestAsRead()
                            }

                            showAcceptFriendRequestDialog()
                        }
                    }

                    is GetProfileDetailsPartialState.Error -> {
                        setState {
                            copy(
                                isLoading = false,
                                fullScreenErrorView = ProfileDetailsFullScreenErrorView(
                                    errorTitle = resourceProvider.getString(R.string.profile_details_screen_error_loading_profile_details_generic),
                                    errorSubtitle = resourceProvider.getGenericErrorMessage(),
                                    onCloseClicked = ProfileDetailsContract.Event.OnBackClicked,
                                    errorButtonLayout = ErrorButtonLayout.Single(
                                        primaryButtonLabel = resourceProvider.getString(R.string.profile_details_screen_label_full_screen_error_view_button_go_back),
                                        onPrimaryButtonClicked = {
                                            setEffect {
                                                ProfileDetailsContract.Effect.Navigation.PopBackStack
                                            }
                                        }
                                    )
                                )
                            )
                        }
                    }

                    is GetProfileDetailsPartialState.NoInternetConnection -> {
                        setState {
                            copy(
                                isLoading = false,
                                fullScreenErrorView = ProfileDetailsFullScreenErrorView(
                                    errorTitle = resourceProvider.getString(R.string.profile_details_screen_error_loading_profile_details_generic),
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

                    is GetProfileDetailsPartialState.Failed -> {
                        when (it.profileDetailsErrorType) {
                            ProfileDetailsErrorType.Blocked -> {
                                setState {
                                    copy(
                                        isLoading = false,
                                        nestedErrorView = ProfileDetailsNestedErrorView(
                                            errorLabel = resourceProvider.getString(R.string.profile_details_screen_error_loading_profile_details_user_blocked),
                                            errorResourceId = R.raw.lottie_not_available
                                        )
                                    )
                                }
                            }

                            ProfileDetailsErrorType.InternalServerError -> {
                                setState {
                                    copy(
                                        isLoading = false,
                                        fullScreenErrorView = ProfileDetailsFullScreenErrorView(
                                            errorTitle = resourceProvider.getString(R.string.profile_details_screen_error_loading_profile_details_generic),
                                            errorSubtitle = resourceProvider.getString(R.string.internal_server_error),
                                            onCloseClicked = ProfileDetailsContract.Event.OnBackClicked,
                                            errorButtonLayout = ErrorButtonLayout.Single(
                                                primaryButtonLabel = resourceProvider.getString(R.string.profile_details_screen_label_full_screen_error_view_button_go_back),
                                                onPrimaryButtonClicked = {
                                                    setEffect {
                                                        ProfileDetailsContract.Effect.Navigation.PopBackStack
                                                    }
                                                }
                                            )
                                        )
                                    )
                                }
                            }

                            ProfileDetailsErrorType.NotExists -> {
                                setState {
                                    copy(
                                        isLoading = false,
                                        nestedErrorView = ProfileDetailsNestedErrorView(
                                            errorLabel = resourceProvider.getString(R.string.profile_details_screen_error_loading_profile_details_user_not_exists),
                                            errorResourceId = R.raw.lottie_error_not_found
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Initializes the more actions item bottom sheet only once. Some menu items may not be shown
     * based on the given [isDifferentUser].
     */
    private fun initializeMoreActionItems(isDifferentUser: Boolean) {
        if (currentState.moreActionItems.isNotEmpty()) return
        val items = listOf(
            BottomSheetContentItem(
                title = resourceProvider.getString(R.string.profile_details_screen_label_more_actions_block),
                titleColor = getErrorColor(),
                onClickEvent = ProfileDetailsContract.Event.MoreActions.OnBlockClicked
            ),
            BottomSheetContentItem(
                title = resourceProvider.getString(R.string.profile_details_screen_label_more_actions_share_profile),
                onClickEvent = ProfileDetailsContract.Event.MoreActions.OnShareProfileClicked
            ),
            BottomSheetContentItem(
                title = resourceProvider.getString(R.string.profile_details_screen_label_more_actions_show_qr_code),
                onClickEvent = ProfileDetailsContract.Event.MoreActions.OnShowQrCodeClicked
            ),
        )
        setState {
            copy(
                moreActionItems = if (isDifferentUser) items.dropLast(1) else items.subList(
                    1,
                    items.size
                )
            )
        }
    }

    /**
     * Attempts to generate a QR code bitmap with the given [content]. If the QR generation fails,
     * then `null` is returned.
     *
     * @param content   The content of the QR code.
     * @return          The generated QR code bitmap, or `null` if generation failed.
     */
    private fun generateQRCodeBitmap(content: String): Bitmap? {
        val size = ConstantsUi.ProfileDetailsScreen.QR_CODE_SIZE
        val blackArgb = Color.Black.toArgb()
        val whiteArgb = Color.White.toArgb()

        return try {
            val bitMatrix =
                MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, size, size)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val pixels = IntArray(width * height)

            for (y in 0 until height) {
                for (x in 0 until width) {
                    pixels[y * width + x] = if (bitMatrix[x, y]) blackArgb else whiteArgb
                }
            }

            createBitmap(width, height).apply {
                setPixels(pixels, 0, width, 0, 0, width, height)
            }
        } catch (e: WriterException) {
            appLogger.e("ProfileDetailsViewModel") { "(generateQRCodeBitmap) Failed to generate QR code for content: $content. Error: $e" }
            null
        }
    }

    /**
     * Triggered when the user clicks on the "block" button.
     */
    private fun onBlockConfirmClicked() {
        hideAlertDialogError()

        setState {
            copy(
                isLoading = true
            )
        }
        viewModelScope.launch {
            blockedUserInteractor.blockUser(currentState.userId).collect {
                when (it) {
                    is BlockUserPartialState.Success -> {
                        // TODO: Use global snackbar.
                        showSnackbar(
                            message = resourceProvider.getString(R.string.profile_details_screen_label_success_block_user),
                            type = SnackbarType.SUCCESS
                        )
                        setEffect {
                            ProfileDetailsContract.Effect.Navigation.PopBackStack
                        }
                    }

                    is BlockUserPartialState.Failed -> {
                        when (it.userBlockResultType) {
                            UserBlockResultType.Failed -> {
                                setState {
                                    copy(
                                        isLoading = false
                                    )
                                }
                                showErroneousSnackbar(message = resourceProvider.getString(R.string.profile_details_screen_error_blocking_user_generic))
                            }

                            UserBlockResultType.SameUser -> {
                                setState {
                                    copy(
                                        isLoading = false
                                    )
                                }
                                showSnackbar(
                                    message = resourceProvider.getString(R.string.profile_details_screen_error_blocking_user_same_user),
                                    type = SnackbarType.INFO
                                )
                            }
                        }
                    }

                    is BlockUserPartialState.Error -> {
                        setState {
                            copy(
                                isLoading = false
                            )
                        }
                        showErroneousSnackbar(message = resourceProvider.getString(R.string.profile_details_screen_error_blocking_user_generic))
                    }
                }
            }
        }
    }

    /**
     * Deletes this friend request.
     */
    private fun deletePendingFriendRequest() {
        setState {
            copy(
                isRelationshipStatusChipLoading = true
            )
        }
        hideAlertDialogError()

        viewModelScope.launch {
            friendRequestInteractor.deleteFriendRequest(
                userId = currentState.userId,
                deleteFriendRequestType = DeleteFriendRequestType.SENDER
            ).collect { wasDeleted ->
                when (wasDeleted) {
                    true -> {
                        setState {
                            copy(
                                isRelationshipStatusChipLoading = false,
                                profileDetails = profileDetails?.copy(
                                    relationshipStatus = RelationshipStatusDomain.None
                                )
                            )
                        }
                    }

                    false -> {
                        setState {
                            copy(
                                isRelationshipStatusChipLoading = false
                            )
                        }
                        showErroneousSnackbar(message = resourceProvider.getString(R.string.profile_details_screen_error_deleting_friend_request))
                    }
                }
            }
        }
    }

    /**
     * Accepts this friend request.
     */
    private fun acceptFriendRequest() {
        setState {
            copy(
                isRelationshipStatusChipLoading = true
            )
        }
        hideAlertDialogError()

        viewModelScope.launch {
            friendRequestInteractor.acceptFriendRequest(currentState.userId)
                .collect { wasAccepted ->
                    when (wasAccepted) {
                        true -> {
                            setState {
                                copy(
                                    isRelationshipStatusChipLoading = false,
                                    profileDetails = profileDetails?.copy(
                                        relationshipStatus = RelationshipStatusDomain.Friends,
                                        userStats = profileDetails.userStats.copy(
                                            friendsCount = profileDetails.userStats.friendsCount + 1
                                        )
                                    )
                                )
                            }
                        }

                        false -> {
                            setState {
                                copy(
                                    isRelationshipStatusChipLoading = false
                                )
                            }
                            showErroneousSnackbar(resourceProvider.getString(R.string.profile_details_screen_error_accepting_friend_request))
                        }
                    }
                }
        }
    }

    /**
     * Deletes this friend.
     */
    private fun deleteFriend() {
        setState {
            copy(
                isRelationshipStatusChipLoading = true
            )
        }
        hideAlertDialogError()

        viewModelScope.launch {
            friendRepository.deleteFriend(currentState.userId).collect { wasDeleted ->
                when (wasDeleted) {
                    true -> {
                        setState {
                            copy(
                                isRelationshipStatusChipLoading = false,
                                profileDetails = profileDetails?.copy(
                                    relationshipStatus = RelationshipStatusDomain.None,
                                    userStats = profileDetails.userStats.copy(
                                        friendsCount = profileDetails.userStats.friendsCount - 1
                                    )
                                )
                            )
                        }
                    }

                    false -> {
                        setState {
                            copy(
                                isRelationshipStatusChipLoading = false
                            )
                        }
                        showErroneousSnackbar(message = resourceProvider.getString(R.string.profile_details_screen_error_deleting_friend))
                    }
                }
            }
        }
    }

    /**
     * Shows the bottom sheet of type [bottomSheetType].
     */
    private fun showBottomSheet(bottomSheetType: ProfileDetailsBottomSheetType) {
        setState {
            copy(
                bottomSheetType = bottomSheetType
            )
        }
        setEffect {
            ProfileDetailsContract.Effect.ShowBottomSheet
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
                        label = resourceProvider.getString(R.string.generic_ok_button),
                        onClick = ::hideAlertDialogError
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
    private fun showSnackbar(message: String, type: SnackbarType) {
        val newSnackbarUuid = UUID.randomUUID()
        val snackbarStateData = SnackbarStateData(
            uuid = newSnackbarUuid,
            message = message,
            type = type,
            onDismiss = {
                if (currentState.snackbarStateData?.uuid == newSnackbarUuid) {
                    setState {
                        copy(
                            snackbarStateData = null
                        )
                    }
                }
            }
        )

        setState {
            copy(
                snackbarStateData = snackbarStateData
            )
        }
        setEffect {
            ProfileDetailsContract.Effect.ShowSnackbar(snackbarStateData)
        }
    }

    private fun showErroneousSnackbar(message: String) {
        showSnackbar(
            message = message,
            type = SnackbarType.ERROR
        )
    }

    private fun hideSnackbar() {
        setEffect {
            ProfileDetailsContract.Effect.HideSnackbar
        }
    }

    /**
     * Navigates the user to the `ProfileDetailsStats` screen.
     */
    private fun navigateToProfileDetailsStatsScreen(tab: ProfileDetailsStatsTab) {
        setEffect {
            ProfileDetailsContract.Effect.Navigation.NavigateToProfileDetailsStatsScreen(
                fullName = currentState.profileDetails?.basicProfileDetails?.fullName.orEmpty(),
                userId = currentState.userId.value,
                tab = tab
            )
        }
    }

    private fun showAcceptFriendRequestDialog() {
        showAlertDialogError(
            ProfileDetailsAlertDialogErrorData(
                errorTitle = resourceProvider.getString(R.string.profile_details_screen_label_alert_dialog_title_accept_friend_request),
                errorSubtitle = resourceProvider.getString(R.string.profile_details_screen_label_alert_dialog_subtitle_accept_friend_request),
                dismissAlertDialogButton = AlertDialogButton(
                    label = resourceProvider.getString(R.string.profile_details_screen_label_alert_dialog_button_negative_accept_friend_request),
                    onClick = ::hideAlertDialogError
                ),
                confirmAlertDialogButton = AlertDialogButton(
                    label = resourceProvider.getString(R.string.profile_details_screen_label_alert_dialog_button_positive_accept_friend_request),
                    buttonType = ErrorButtonType.Contained,
                    onClick = ::acceptFriendRequest
                )
            )
        )
    }
}
