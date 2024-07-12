package com.alexgreece45.marvelx.ui.screens.profile

import android.content.res.Configuration
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.domain.model_api.user.profileDetails.ProfileDetailsDomain
import com.alexgreece45.marvelx.ui.components.AlertDialogError
import com.alexgreece45.marvelx.ui.components.BottomNavigationBarData
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.IconData
import com.alexgreece45.marvelx.ui.components.ProfileHeader
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.WrapAsyncImage
import com.alexgreece45.marvelx.ui.components.WrapModalBottomSheetLayout
import com.alexgreece45.marvelx.ui.components.WrapTextButton
import com.alexgreece45.marvelx.ui.components.effect.ObserveUiEffect
import com.alexgreece45.marvelx.ui.components.error.ContentErrorScreen
import com.alexgreece45.marvelx.ui.components.icons.NavigateIcon
import com.alexgreece45.marvelx.ui.components.modalBottomSheetContents.ModalBottomSheetContentItems
import com.alexgreece45.marvelx.ui.components.model.BottomSheetContentItem
import com.alexgreece45.marvelx.ui.components.model.ErrorButtonLayout
import com.alexgreece45.marvelx.ui.components.model.ShadowData
import com.alexgreece45.marvelx.ui.components.rememberWrapModalBottomSheetState
import com.alexgreece45.marvelx.ui.helper.model.DeepLinkAction
import com.alexgreece45.marvelx.ui.navigation.getBottomNavigationBarData
import com.alexgreece45.marvelx.ui.screens.profile.model.ProfileBottomSheetType
import com.alexgreece45.marvelx.ui.screens.profile.model.ProfileScreenSection
import com.alexgreece45.marvelx.ui.theme.MarvelXTheme
import com.alexgreece45.marvelx.ui.theme.values.customShapes
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.VSpacer
import com.alexgreece45.marvelx.util.constants.ConstantsUi
import com.alexgreece45.marvelx.util.extensions.applyPadding
import com.alexgreece45.marvelx.util.extensions.circularImageWithBorder
import com.alexgreece45.marvelx.util.extensions.getPendingDeepLinkAction
import com.alexgreece45.marvelx.util.extensions.hideIfVisible
import com.alexgreece45.marvelx.util.extensions.screenPaddingsNone
import com.alexgreece45.marvelx.util.extensions.throttledClickable
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProfileScreen(
    onEventSent: (ProfileContract.Event) -> Unit,
    state: ProfileContract.State,
    effect: Flow<ProfileContract.Effect>,
    bottomNavigationBarData: @Composable () -> BottomNavigationBarData,
    onNavigation: (ProfileContract.Effect.Navigation) -> Unit,
    openDeepLinkAction: (DeepLinkAction) -> Unit
) {
    val context = LocalContext.current

    val waveShape = MaterialTheme.customShapes.waveShape
    val waveColor = MaterialTheme.colors.surface

    val cropImageCoroutineScope = rememberCoroutineScope()
    val modalBottomSheetCoroutineScope = rememberCoroutineScope()
    val modalBottomSheetState = rememberWrapModalBottomSheetState()

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            onEventSent(ProfileContract.Event.ProfileImage.OnImageSelected(uri))
        }
    )

    val cropImageLauncher = rememberLauncherForActivityResult(
        contract = CropImageContract(),
        onResult = { result ->
            if (result.isSuccessful) {
                onEventSent(ProfileContract.Event.ProfileImage.ImageCropper.OnSuccess(result.uriContent))
            } else {
                onEventSent(ProfileContract.Event.ProfileImage.ImageCropper.OnFailed)
            }
        }
    )

    LaunchedEffect(Unit) {
        onEventSent(ProfileContract.Event.Init(context.getPendingDeepLinkAction()))
    }

    ObserveUiEffect(effect) { uiEffect ->
        when (uiEffect) {
            is ProfileContract.Effect.Navigation -> {
                onNavigation(uiEffect)
            }

            is ProfileContract.Effect.ShowToast -> {
                Toast.makeText(context, uiEffect.toastMessage, Toast.LENGTH_LONG).show()
            }

            is ProfileContract.Effect.OpenDeepLinkAction -> {
                openDeepLinkAction(uiEffect.deepLinkAction)
            }

            is ProfileContract.Effect.OpenPhotoPicker -> {
                singlePhotoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }

            ProfileContract.Effect.BottomSheet.Show -> {
                modalBottomSheetCoroutineScope.launch {
                    modalBottomSheetState.show()
                }
            }

            ProfileContract.Effect.BottomSheet.Hide -> {
                modalBottomSheetCoroutineScope.launch {
                    modalBottomSheetState.hideIfVisible()
                }
            }

            is ProfileContract.Effect.OpenImageCropper -> {
                cropImageCoroutineScope.launch {
                    cropImageLauncher.launch(
                        CropImageContractOptions(
                            uri = uiEffect.imageUri,
                            cropImageOptions = CropImageOptions(
                                cropShape = CropImageView.CropShape.OVAL,
                                guidelines = CropImageView.Guidelines.ON_TOUCH,
                                autoZoomEnabled = false,
                                multiTouchEnabled = true,
                                maxZoom = 3,
                                minCropResultWidth = uiEffect.imageSize,
                                minCropResultHeight = uiEffect.imageSize,
                                maxCropResultWidth = uiEffect.imageSize,
                                maxCropResultHeight = uiEffect.imageSize,
                                activityTitle = uiEffect.toolbarTitle,
                                outputCompressQuality = 100,
                            )
                        )
                    )
                }
            }
        }
    }

    WrapModalBottomSheetLayout(
        sheetContent = {
            when (state.bottomSheetType) {
                ProfileBottomSheetType.ProfileImage -> {
                    BottomSheetProfileImage(
                        bottomSheetContentItems = state.profileImageBottomSheetContentItems,
                        onEventSent = onEventSent
                    )
                }

                null -> {}
            }
        },
        sheetState = modalBottomSheetState
    ) {
        ContentScreen(
            isLoading = state.isLoading,
            bottomNavigationBarData = bottomNavigationBarData(),
            paddingValues = PaddingValues.Absolute()
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
                    .drawBehind {
                        drawOutline(
                            outline = waveShape.createOutline(
                                size = Size(width = size.width, height = size.height * 0.6f),
                                layoutDirection = layoutDirection,
                                density = this
                            ),
                            color = waveColor,
                            style = Fill
                        )
                    }
            ) {
                // Banner.
                ProfileBanner(
                    welcomeMessageLabel = state.profileBannerLabel,
                    imageUrl = state.profileDetails?.profileImageUrl.orEmpty(),
                    onSeeYourProfileClicked = { onEventSent(ProfileContract.Event.OnSeeYourProfileClicked) },
                    onProfileImageEventSent = { onEventSent(it) }
                )

//            VSpacer.Custom(height = 100) // todo: to be replaced with weights

                // Sections.
                Column(
                    modifier = Modifier.padding(screenPaddingsNone().applyPadding()),
                    verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp)
                ) {
                    state.sections.forEach { section -> ProfileSection(section = section) }
                }
            }
        }
    }

    // Alert Dialog error.
    state.errorAlertDialog?.let { errorAlertDialog ->
        AlertDialogError(
            isVisible = true,
            title = errorAlertDialog.errorTitle,
            errorMessage = errorAlertDialog.errorSubtitle,
            confirmButton = errorAlertDialog.alertDialogButtonPositive,
            dismissButton = errorAlertDialog.alertDialogButtonNegative
        )
    }

    // Error screen.
    state.error?.let { error ->
        ContentErrorScreen(
            errorTitle = error.errorTitle,
            errorSubtitle = error.errorSubtitle,
            onClose = { onEventSent(ProfileContract.Event.OnErrorClose) },
            errorButtonLayout = ErrorButtonLayout.Single(
                primaryButtonLabel = stringResource(R.string.generic_retry_button),
                onPrimaryButtonClicked = { onEventSent(error.event) }
            )
        )
    }

    // Cropped image result screen.
    state.croppedImageUri?.let { imageUri ->
        CroppedImage(
            isLoading = state.croppedImageIsLoading,
            imageUri = imageUri,
            onCloseClicked = { onEventSent(ProfileContract.Event.ProfileImage.CroppedImage.OnCloseClicked) },
            onSaveClicked = { onEventSent(ProfileContract.Event.ProfileImage.CroppedImage.OnSaveClicked) }
        )
    }

    BackHandler {
        onEventSent(ProfileContract.Event.OnSystemBack)
    }
}

/**
 * The profile image bottom sheet.
 */
@Composable
private fun BottomSheetProfileImage(
    bottomSheetContentItems: List<BottomSheetContentItem>,
    onEventSent: (ProfileContract.Event) -> Unit,
) = ModalBottomSheetContentItems(
    bottomSheetContentItems = bottomSheetContentItems,
    onEventSent = onEventSent
)

/**
 * Displays the cropped version of the user-selected image. The user through this composable
 * function can set the cropped image as their current profile image.
 */
@Composable
private fun CroppedImage(
    isLoading: Boolean,
    imageUri: Uri,
    onCloseClicked: () -> Unit,
    onSaveClicked: () -> Unit
) {
    ContentScreen(
        isLoading = isLoading,
        showToolbar = true,
        toolbarTitle = stringResource(R.string.profile_screen_cropped_cropped_image_toolbar_title),
        navigableAction = ScreenNavigableAction.Closeable(onClose = onCloseClicked),
        toolbarActionsContent = {
            WrapTextButton(onClick = onSaveClicked) {
                Text(
                    text = stringResource(R.string.profile_screen_cropped_cropped_image_label_button_save),
                    style = MaterialTheme.typography.h6
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WrapAsyncImage(
                imageUrl = imageUri.toString(),
                contentDescription = stringResource(R.string.profile_screen_content_description_cropped_image_new_profile_image),
                modifier = Modifier
                    .size(ConstantsUi.PROFILE_IMAGE_FULL_SIZE)
                    .clip(CircleShape),
                contentScale = ContentScale.Fit,
            )
        }
    }
}

/**
 * Displays the profile banner that includes the first name of the user, and their profile image.
 */
@Composable
private fun ProfileBanner(
    welcomeMessageLabel: String,
    imageUrl: String,
    onSeeYourProfileClicked: () -> Unit,
    onProfileImageEventSent: (ProfileContract.Event.ProfileImage) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = ConstantsUi.PROFILE_IMAGE_NORMAL_SIZE * 0.3f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile image.
        Box(
            modifier = Modifier.throttledClickable(
                view = LocalView.current,
                showRipple = false,
                onClick = { onProfileImageEventSent(ProfileContract.Event.ProfileImage.OnClicked) }
            )
        ) {
            WrapAsyncImage(
                imageUrl = imageUrl,
                contentDescription = stringResource(R.string.content_description_your_profile_image),
                modifier = Modifier
                    .size(ConstantsUi.PROFILE_IMAGE_NORMAL_SIZE)
                    .circularImageWithBorder(
                        borderWidth = ConstantsUi.IMAGE_BORDER_WIDTH,
                        borderColor = MaterialTheme.colors.error
                    ),
                placeholder = painterResource(R.drawable.ic_person),
                error = painterResource(R.drawable.ic_person),
                onSuccess = { onProfileImageEventSent(ProfileContract.Event.ProfileImage.OnLoaded) },
                onError = { onProfileImageEventSent(ProfileContract.Event.ProfileImage.OnLoaded) },
                contentScale = ContentScale.Fit,
                shadowData = ShadowData(
                    elevation = ConstantsUi.ProfileScreen.PROFILE_IMAGE_ELEVATION,
                    shape = CircleShape,
                    clip = true,
                    ambientColor = MaterialTheme.colors.error,
                    spotColor = MaterialTheme.colors.error
                )
            )

            Icon(
                painter = painterResource(R.drawable.ic_add),
                contentDescription = stringResource(R.string.content_description_upload_profile_image),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(SPACING_SMALL.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colors.background)
            )
        }

        VSpacer.Small()

        // Welcome message label.
        Text(
            text = welcomeMessageLabel,
            style = MaterialTheme.typography.h5
        )

        // See your profile text button.
        Row(
            modifier = Modifier.throttledClickable(
                view = LocalView.current,
                onClick = onSeeYourProfileClicked,
                showRipple = false
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.profile_screen_profile_banner_label_see_your_profile),
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.subtitle2
            )

            NavigateIcon(
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colors.error
            )
        }
    }
}

/**
 * Displays a single profile section.
 */
@Composable
private fun ProfileSection(
    section: ProfileScreenSection
) {
    ProfileHeader(
        title = section.title,
        leadingIcon = {
            Icon(
                painter = painterResource(section.iconData.iconResource),
                contentDescription = stringResource(
                    section.iconData.contentDescription
                )
            )
        },
        trailingIcon = {
            if (section.isNavigable) {
                NavigateIcon()
            }
        },
        onClick = section.onClick
    )
}

@Preview(
    apiLevel = 34,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun ProfileScreenPreview() {
    val profileDetails = ProfileDetailsDomain.mockSameUser()
    MarvelXTheme {
        ProfileScreen(
            onEventSent = {},
            state = ProfileContract.State(
                isLoading = false,
                sections = listOf(
                    ProfileScreenSection(
                        title = stringResource(R.string.profile_screen_section_label_privacy_settings),
                        iconData = IconData(
                            iconResource = R.drawable.ic_privacy,
                            contentDescription = R.string.content_description_privacy
                        ),
                        onClick = { }
                    ),
                    ProfileScreenSection(
                        title = stringResource(R.string.profile_screen_section_label_faq),
                        iconData = IconData(
                            iconResource = R.drawable.ic_faq,
                            contentDescription = R.string.content_description_faq
                        ),
                        onClick = { }
                    ),
                    ProfileScreenSection(
                        title = stringResource(R.string.profile_screen_section_label_logout),
                        iconData = IconData(
                            iconResource = R.drawable.ic_logout,
                            contentDescription = R.string.content_description_ic_logout
                        ),
                        isNavigable = false,
                        onClick = { }
                    ),
                ),
                profileBannerLabel = stringResource(
                    R.string.profile_screen_profile_banner_label_welcome_back,
                    profileDetails.basicProfileDetails.firstName
                ),
                profileDetails = profileDetails.basicProfileDetails
            ),
            effect = emptyFlow(),
            bottomNavigationBarData = { getBottomNavigationBarData(navController = rememberNavController()) },
            onNavigation = {},
            openDeepLinkAction = {},
        )
    }
}
