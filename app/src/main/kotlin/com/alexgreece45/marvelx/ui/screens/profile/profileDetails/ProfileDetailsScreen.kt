package com.alexgreece45.marvelx.ui.screens.profile.profileDetails

import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.common_ui.extensions.hideSnackbar
import com.alexgreece45.marvelx.common_ui.extensions.showSnackbarAndHandleResult
import com.alexgreece45.marvelx.common_ui.previewAnnotation.ThemePreview
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarConfig
import com.alexgreece45.marvelx.domain.model_api.user.profileDetails.ProfileDetailsDomain
import com.alexgreece45.marvelx.domain.model_api.user.profileDetails.RelationshipStatusDomain
import com.alexgreece45.marvelx.ui.components.AlertDialogError
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.IconData
import com.alexgreece45.marvelx.ui.components.ModalBottomSheetContent
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.ToolbarAction
import com.alexgreece45.marvelx.ui.components.WrapAsyncImage
import com.alexgreece45.marvelx.ui.components.WrapHorizontalDivider
import com.alexgreece45.marvelx.ui.components.WrapModalBottomSheetLayout
import com.alexgreece45.marvelx.ui.components.chips.AddFriendChip
import com.alexgreece45.marvelx.ui.components.chips.CancelRequestChip
import com.alexgreece45.marvelx.ui.components.chips.FriendsChip
import com.alexgreece45.marvelx.ui.components.chips.IncomingFriendRequestChip
import com.alexgreece45.marvelx.ui.components.effect.ObserveUiEffect
import com.alexgreece45.marvelx.ui.components.enforceMaxBrightness
import com.alexgreece45.marvelx.ui.components.error.ContentErrorScreen
import com.alexgreece45.marvelx.ui.components.modalBottomSheetContents.ModalBottomSheetContentItems
import com.alexgreece45.marvelx.ui.components.model.BottomSheetContentItem
import com.alexgreece45.marvelx.ui.components.rememberWrapModalBottomSheetState
import com.alexgreece45.marvelx.ui.model.Gender
import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.model.ProfileDetailsBottomSheetType
import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.model.ProfileDetailsNestedErrorView
import com.alexgreece45.marvelx.ui_theme.theme.MarvelXTheme
import com.alexgreece45.marvelx.ui_theme.theme.values.customColors
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.VSpacer
import com.alexgreece45.marvelx.util.constants.ConstantsUi
import com.alexgreece45.marvelx.util.extensions.circularImageWithBorder
import com.alexgreece45.marvelx.util.extensions.hideIfVisible
import com.alexgreece45.marvelx.util.extensions.setText
import com.alexgreece45.marvelx.util.extensions.shareContentUrl
import com.alexgreece45.marvelx.util.extensions.throttledClickable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun ProfileDetailsScreen(
    onEventSent: (ProfileDetailsContract.Event) -> Unit,
    state: ProfileDetailsContract.State,
    effect: Flow<ProfileDetailsContract.Effect>,
    userId: String,
    shouldShowAcceptFriendRequestDialog: Boolean,
    onNavigation: (ProfileDetailsContract.Effect.Navigation) -> Unit
) {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val clipboard = LocalClipboard.current

    val modalBottomSheetState = rememberWrapModalBottomSheetState()
    val snackbarHostState = remember { SnackbarHostState() }

    /**
     * Whether to enforce the max screen brightness.
     */
    var shouldEnforceMaxBrightness by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        onEventSent(ProfileDetailsContract.Event.Init(userId, shouldShowAcceptFriendRequestDialog))
    }

    LaunchedEffect(modalBottomSheetState.currentValue, state.bottomSheetType) {
        shouldEnforceMaxBrightness =
            modalBottomSheetState.currentValue != ModalBottomSheetValue.Hidden
                    && state.bottomSheetType is ProfileDetailsBottomSheetType.QrCode
        enforceMaxBrightness(context, shouldEnforceMaxBrightness)
    }

    ObserveUiEffect(effect) { uiEffect ->
        when (uiEffect) {
            is ProfileDetailsContract.Effect.Navigation -> onNavigation(uiEffect)

            ProfileDetailsContract.Effect.ShowBottomSheet -> {
                coroutineScope.launch {
                    modalBottomSheetState.show()
                }
            }

            ProfileDetailsContract.Effect.HideBottomSheet -> {
                coroutineScope.launch {
                    modalBottomSheetState.hideIfVisible()
                }
            }

            is ProfileDetailsContract.Effect.ShareProfileUrl -> {
                context.shareContentUrl(
                    url = uiEffect.url,
                    title = uiEffect.intentTitle
                )
            }

            is ProfileDetailsContract.Effect.SetClipboardText -> {
                clipboard.setText(uiEffect.text)
                Toast.makeText(context, uiEffect.toastMessage, Toast.LENGTH_SHORT).show()
            }

            is ProfileDetailsContract.Effect.ShowSnackbar -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbarAndHandleResult(uiEffect.snackbarStateData)
                }
            }

            ProfileDetailsContract.Effect.HideSnackbar -> {
                snackbarHostState.hideSnackbar()
            }
        }
    }

    BackHandler {
        onEventSent(ProfileDetailsContract.Event.OnSystemBack)
    }

    WrapModalBottomSheetLayout(
        sheetState = modalBottomSheetState,
        sheetContent = {
            when (val bottomSheetType = state.bottomSheetType) {
                ProfileDetailsBottomSheetType.MoreActions -> {
                    BottomSheetMoreActions(
                        moreActionItems = state.moreActionItems,
                        onEventSent = onEventSent
                    )
                }

                is ProfileDetailsBottomSheetType.QrCode -> {
                    BottomSheetQrCode(
                        qrCodeBitmap = bottomSheetType.qrBitmap,
                        fullName = state.profileDetails?.basicProfileDetails?.fullName.orEmpty()
                    )
                }

                null -> {}
            }
        }
    ) {
        ContentScreen(
            isLoading = state.isLoading,
            navigableAction = ScreenNavigableAction.Backable(
                onBack = {
                    onEventSent(
                        ProfileDetailsContract.Event.OnBackClicked
                    )
                }
            ),
            showToolbar = true,
            toolbarTitle = state.profileDetails?.basicProfileDetails?.fullName,
            toolbarActions = when (state.profileDetails?.isDifferentUser) {
                null -> emptyList()

                true -> listOf(
                    ToolbarAction(
                        label = stringResource(id = R.string.content_description_more_actions),
                        order = 0,
                        iconData = IconData(
                            iconResource = R.drawable.ic_more,
                            contentDescription = R.string.content_description_ic_more_actions
                        ),
                        onClick = { onEventSent(ProfileDetailsContract.Event.MoreActions.OnClicked) }
                    )
                )

                false -> listOf(
                    ToolbarAction(
                        label = stringResource(R.string.profile_details_screen_content_description_edit_profile_details),
                        order = 0,
                        iconData = IconData(
                            iconResource = R.drawable.ic_edit,
                            contentDescription = R.string.profile_details_screen_content_description_edit_profile_details
                        ),
                        onClick = { onEventSent(ProfileDetailsContract.Event.OnEditProfileDetailsIconClicked) }
                    ),
                    ToolbarAction(
                        label = stringResource(id = R.string.content_description_more_actions),
                        order = 1,
                        iconData = IconData(
                            iconResource = R.drawable.ic_more,
                            contentDescription = R.string.content_description_ic_more_actions
                        ),
                        onClick = { onEventSent(ProfileDetailsContract.Event.MoreActions.OnClicked) }
                    )
                )
            },
            snackbarConfig = SnackbarConfig(
                snackbarHostState = snackbarHostState,
                snackbarStateData = state.snackbarStateData
            ),
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
            ) {
                // Profile details.
                state.profileDetails?.let { profileDetails ->
                    // Profile Banner.
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(ConstantsUi.ProfileDetailsScreen.PROFILE_BANNER_PADDING),
                            verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp)
                        ) {
                            // Profile image and lists.
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Profile image.
                                WrapAsyncImage(
                                    imageUrl = profileDetails.basicProfileDetails.profileImageUrlData.imageUrl,
                                    contentDescription = stringResource(R.string.content_description_your_profile_image),
                                    modifier = Modifier
                                        .size(ConstantsUi.PROFILE_IMAGE_SMALL_SIZE)
                                        .throttledClickable(
                                            view = LocalView.current,
                                            onClick = { onEventSent(ProfileDetailsContract.Event.ProfileBanner.OnProfileImageClicked) }
                                        )
                                        .circularImageWithBorder(
                                            borderWidth = ConstantsUi.IMAGE_BORDER_WIDTH,
                                            borderColor = MaterialTheme.customColors.genericRed
                                        ),
                                    placeholder = painterResource(R.drawable.ic_person),
                                    error = painterResource(R.drawable.ic_person),
                                    contentScale = ContentScale.FillBounds
                                )

                                // Profile Banner tiles.
                                Layout(
                                    content = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(32.dp)
                                        ) {
                                            ProfileBannerTile(
                                                label = stringResource(R.string.profile_details_screen_label_profile_banner_tile_friends),
                                                count = profileDetails.userStats.friendsCountFormatted,
                                                onClick = { onEventSent(ProfileDetailsContract.Event.ProfileBanner.OnFriendsListClicked) }
                                            )

                                            ProfileBannerTile(
                                                label = stringResource(R.string.profile_details_screen_label_profile_banner_tile_listings),
                                                count = profileDetails.userStats.listingsCountFormatted,
                                                onClick = { onEventSent(ProfileDetailsContract.Event.ProfileBanner.OnListingsListClicked) }
                                            )

                                            ProfileBannerTile(
                                                label = stringResource(R.string.profile_details_screen_label_profile_banner_tile_favorites),
                                                count = profileDetails.userStats.favoritesCountFormatted,
                                                onClick = { onEventSent(ProfileDetailsContract.Event.ProfileBanner.OnFavoritesListClicked) }
                                            )
                                        }
                                    }
                                ) { measurables, constraints ->
                                    val tilePlaceable = measurables[0].measure(constraints)

                                    val remainingSpace = constraints.maxWidth - tilePlaceable.width

                                    val tileOffset = (remainingSpace * 0.55f).toInt()

                                    layout(
                                        width = constraints.maxWidth,
                                        height = tilePlaceable.height
                                    ) {
                                        tilePlaceable.placeRelative(tileOffset, 0)
                                    }
                                }
                            }

                            // Gender and email.
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Column(verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)) {
                                    // Gender.
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (state.isGenderHidden) {
                                            // Hidden gender.
                                            Icon(
                                                painter = painterResource(R.drawable.ic_person),
                                                contentDescription = stringResource(R.string.content_description_gender_icon_other)
                                            )
                                            HiddenField(label = stringResource(R.string.profile_details_screen_label_gender_hidden))
                                        } else {
                                            when (profileDetails.basicProfileDetails.gender) {
                                                Gender.Other -> {
                                                    Icon(
                                                        painter = painterResource(R.drawable.ic_person),
                                                        contentDescription = stringResource(R.string.content_description_gender_icon_other)
                                                    )
                                                }

                                                Gender.Female -> {
                                                    Icon(
                                                        painter = painterResource(R.drawable.ic_female),
                                                        contentDescription = stringResource(R.string.content_description_gender_icon_female),
                                                        tint = MaterialTheme.customColors.genderFemale
                                                    )
                                                }

                                                Gender.Male -> {
                                                    Icon(
                                                        painter = painterResource(R.drawable.ic_male),
                                                        contentDescription = stringResource(R.string.content_description_gender_icon_male),
                                                        tint = MaterialTheme.customColors.genderMale
                                                    )
                                                }
                                            }
                                            VisibleField(
                                                label = stringResource(profileDetails.basicProfileDetails.gender.label),
                                                onLongClickLabel = stringResource(R.string.screen_profile_details_label_gender_copy),
                                                onLongClick = { onEventSent(ProfileDetailsContract.Event.ProfileBanner.OnGenderLongClick) }
                                            )
                                        }
                                    }

                                    // Email.
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_email),
                                            contentDescription = stringResource(R.string.content_description_icon_email)
                                        )
                                        if (state.isEmailHidden) {
                                            // Hidden email.
                                            HiddenField(label = stringResource(R.string.profile_details_screen_label_email_hidden))
                                        } else {
                                            VisibleField(
                                                label = profileDetails.basicProfileDetails.email,
                                                onLongClickLabel = stringResource(R.string.screen_profile_details_label_email_copy),
                                                onLongClick = { onEventSent(ProfileDetailsContract.Event.ProfileBanner.OnEmailLongClick) }
                                            )
                                        }
                                    }
                                }
                            }

                            // Relationship status.
                            if (profileDetails.isDifferentUser) {
                                WrapHorizontalDivider()

                                when (profileDetails.relationshipStatus) {
                                    null -> {}

                                    RelationshipStatusDomain.None -> {
                                        AddFriendChip(
                                            onClick = { onEventSent(ProfileDetailsContract.Event.ProfileBanner.OnRelationshipStatusChipClicked) },
                                            isLoading = state.isRelationshipStatusChipLoading
                                        )
                                    }

                                    RelationshipStatusDomain.PendingFriendRequestSender -> {
                                        CancelRequestChip(
                                            onClick = { onEventSent(ProfileDetailsContract.Event.ProfileBanner.OnRelationshipStatusChipClicked) },
                                            isLoading = state.isRelationshipStatusChipLoading
                                        )
                                    }

                                    RelationshipStatusDomain.PendingFriendRequestReceiver -> {
                                        IncomingFriendRequestChip(
                                            onClick = { onEventSent(ProfileDetailsContract.Event.ProfileBanner.OnRelationshipStatusChipClicked) },
                                            isLoading = state.isRelationshipStatusChipLoading
                                        )
                                    }

                                    RelationshipStatusDomain.Friends -> {
                                        FriendsChip(
                                            onClick = { onEventSent(ProfileDetailsContract.Event.ProfileBanner.OnRelationshipStatusChipClicked) },
                                            isLoading = state.isRelationshipStatusChipLoading
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // User does not exist or is blocked error view.
                state.nestedErrorView?.let { nestedErrorView ->
                    NestedErrorView(
                        nestedErrorView = nestedErrorView
                    )
                }
            }
        }
    }

    state.fullScreenErrorView?.let { errorView ->
        ContentErrorScreen(
            errorTitle = errorView.errorTitle,
            errorSubtitle = errorView.errorSubtitle,
            onClose = { onEventSent(errorView.onCloseClicked) },
            errorButtonLayout = errorView.errorButtonLayout
        )
    }

    state.alertDialogError?.let { alertDialogData ->
        AlertDialogError(
            isVisible = true,
            title = alertDialogData.errorTitle,
            errorMessage = { Text(text = alertDialogData.errorSubtitle) },
            dismissButton = alertDialogData.dismissAlertDialogButton,
            confirmButton = alertDialogData.confirmAlertDialogButton
        )
    }
}

/**
 * The content for the `more actions` bottom sheet.
 */
@Composable
private fun BottomSheetMoreActions(
    moreActionItems: List<BottomSheetContentItem>,
    onEventSent: (ProfileDetailsContract.Event) -> Unit
) = ModalBottomSheetContentItems(
    bottomSheetContentItems = moreActionItems,
    onEventSent = onEventSent
)

/**
 * The content for the `QR code` bottom sheet.
 */
@Composable
private fun BottomSheetQrCode(qrCodeBitmap: Bitmap, fullName: String) {
    ModalBottomSheetContent {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.profile_details_screen_label_scan_qr_code),
                style = MaterialTheme.typography.h5
            )

            VSpacer.Small()

            Image(
                bitmap = qrCodeBitmap.asImageBitmap(),
                contentDescription = stringResource(R.string.profile_details_screen_content_description_qr_code),
                modifier = Modifier.aspectRatio(1f)
            )

            VSpacer.Small()

            Text(
                text = fullName,
                style = MaterialTheme.typography.h5
            )
        }
    }
}

/**
 * Provides custom styling to a [Text] composable which is used when a field's value is hidden from
 * the user. The text to show is set through [label].
 */
@Composable
private fun HiddenField(label: String) {
    Text(
        text = label,
        fontStyle = FontStyle.Italic,
        style = MaterialTheme.typography.subtitle1
    )
}

/**
 * Provides custom styling to a [Text] composable which is used when a field's value is visible to
 * the user. The text to show is set through [label].
 */
@Composable
private fun VisibleField(label: String, onLongClickLabel: String, onLongClick: () -> Unit) {
    Text(
        text = label,
        modifier = Modifier.combinedClickable(
            interactionSource = null,
            indication = null,
            role = Role.Button,
            onLongClickLabel = onLongClickLabel,
            onLongClick = onLongClick,
            onClick = {}
        ),
        style = MaterialTheme.typography.subtitle1
    )
}

/**
 * Displays a profile banner tile with the given [label] and [count].
 */
@Composable
private fun ProfileBannerTile(label: String, count: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier.throttledClickable(
            view = LocalView.current,
            onClick = onClick
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count,
            style = MaterialTheme.typography.h6
        )
        Text(
            text = label,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = MaterialTheme.typography.subtitle1
        )
    }
}

/**
 * The nested error view representing the error label when a user was not found, or is blocked.
 */
@Composable
private fun NestedErrorView(
    nestedErrorView: ProfileDetailsNestedErrorView
) {
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(
            resId = nestedErrorView.errorResourceId
        )
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(ConstantsUi.ProfileDetailsScreen.LOTTIE_ANIMATION_SIZE)
        )

        Text(
            text = nestedErrorView.errorLabel,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = MaterialTheme.typography.h5
        )
    }
}

@ThemePreview
@Composable
private fun ProfileDetailsScreenPreview() {
    MarvelXTheme {
        ProfileDetailsScreen(
            onEventSent = {},
            state = ProfileDetailsContract.State(
                isLoading = false,
                profileDetails = ProfileDetailsDomain.mockAnotherUser(),
                isEmailHidden = true,
            ),
            effect = emptyFlow(),
            userId = "",
            shouldShowAcceptFriendRequestDialog = false,
            onNavigation = {}
        )
    }
}

@ThemePreview
@Composable
private fun ProfileDetailsScreenNestedErrorViewPreview() {
    MarvelXTheme {
        ProfileDetailsScreen(
            onEventSent = {},
            state = ProfileDetailsContract.State(
                isLoading = false,
                profileDetails = null,
                isEmailHidden = true,
                nestedErrorView = ProfileDetailsNestedErrorView(
                    errorLabel = stringResource(R.string.profile_details_screen_error_loading_profile_details_user_blocked),
                    errorResourceId = R.raw.lottie_not_available
                )
            ),
            effect = emptyFlow(),
            userId = "100",
            shouldShowAcceptFriendRequestDialog = false,
            onNavigation = {}
        )
    }
}

@ThemePreview
@Composable
private fun ProfileDetailsScreenMoreActionBottomSheetPreview() {
    MarvelXTheme {
        ProfileDetailsScreen(
            onEventSent = {},
            state = ProfileDetailsContract.State(
                isLoading = false,
                profileDetails = ProfileDetailsDomain.mockAnotherUser(),
                isEmailHidden = true,
                moreActionItems = listOf(
                    BottomSheetContentItem(
                        title = stringResource(R.string.profile_details_screen_label_more_actions_block),
                        titleColor = MaterialTheme.customColors.genericRed,
                        onClickEvent = ProfileDetailsContract.Event.MoreActions.OnBlockClicked
                    ),
                    BottomSheetContentItem(
                        title = stringResource(R.string.profile_details_screen_label_more_actions_share_profile),
                        onClickEvent = ProfileDetailsContract.Event.MoreActions.OnShareProfileClicked
                    ),
                    BottomSheetContentItem(
                        title = stringResource(R.string.profile_details_screen_label_more_actions_show_qr_code),
                        onClickEvent = ProfileDetailsContract.Event.MoreActions.OnShowQrCodeClicked
                    ),
                ),
                bottomSheetType = ProfileDetailsBottomSheetType.MoreActions
            ),
            effect = emptyFlow(),
            userId = "101",
            shouldShowAcceptFriendRequestDialog = false,
            onNavigation = {}
        )
    }
}
