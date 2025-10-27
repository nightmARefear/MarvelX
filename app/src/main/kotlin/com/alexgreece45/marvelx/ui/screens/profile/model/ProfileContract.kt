package com.alexgreece45.marvelx.ui.screens.profile.model

import android.net.Uri
import com.alexgreece45.marvelx.domain.model_api.user.profileDetails.BasicProfileDetailsDomain
import com.alexgreece45.marvelx.domain.model_generic.AttachedFileContentData
import com.alexgreece45.marvelx.domain.model_generic.ReadFileContentData
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.ui.components.model.BottomSheetContentItem
import com.alexgreece45.marvelx.ui.components.sections.model.SectionHeaderData
import com.alexgreece45.marvelx.ui.helper.model.DeepLinkAction

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