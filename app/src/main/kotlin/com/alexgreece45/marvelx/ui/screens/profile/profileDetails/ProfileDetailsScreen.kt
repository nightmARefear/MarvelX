package com.alexgreece45.marvelx.ui.screens.profile.profileDetails

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.domain.model_api.user.profileDetails.ProfileDetailsDomain
import com.alexgreece45.marvelx.network_logic.models_api.user.Gender
import com.alexgreece45.marvelx.ui.components.AlertDialogError
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.IconData
import com.alexgreece45.marvelx.ui.components.ModalBottomSheetContent
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.ToolbarAction
import com.alexgreece45.marvelx.ui.components.WrapAsyncImage
import com.alexgreece45.marvelx.ui.components.WrapButton
import com.alexgreece45.marvelx.ui.components.WrapModalBottomSheetLayout
import com.alexgreece45.marvelx.ui.components.effect.ObserveUiEffect
import com.alexgreece45.marvelx.ui.components.enforceMaxBrightness
import com.alexgreece45.marvelx.ui.components.error.ContentErrorScreen
import com.alexgreece45.marvelx.ui.components.modalBottomSheetContents.ModalBottomSheetContentItems
import com.alexgreece45.marvelx.ui.components.model.BottomSheetContentItem
import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.model.ProfileDetailsBottomSheetType
import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.model.ProfileDetailsNestedErrorView
import com.alexgreece45.marvelx.ui.theme.MarvelXTheme
import com.alexgreece45.marvelx.ui.theme.values.customColors
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.VSpacer
import com.alexgreece45.marvelx.util.constants.ConstantsUi
import com.alexgreece45.marvelx.util.extensions.circularImageWithBorder
import com.alexgreece45.marvelx.util.extensions.hideIfVisible
import com.alexgreece45.marvelx.util.extensions.throttledClickable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProfileDetailsScreen(
    onEventSent: (ProfileDetailsContract.Event) -> Unit,
    state: ProfileDetailsContract.State,
    effect: Flow<ProfileDetailsContract.Effect>,
    userId: Int,
    onNavigation: (ProfileDetailsContract.Effect.Navigation) -> Unit
) {
    val context = LocalContext.current

    val bannerShape = MaterialTheme.shapes.large
    val bannerShapeColor = MaterialTheme.colors.surface

    val coroutineScope = rememberCoroutineScope()

    val modalBottomSheetState =
        rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            // Allows to display a bottom sheet directly after the previous one.
            animationSpec = tween(
                durationMillis = 150,
                easing = FastOutLinearInEasing
            ),
            skipHalfExpanded = true
        )

    /**
     * Whether to enforce the max screen brightness.
     */
    var shouldEnforceMaxBrightness by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        onEventSent(ProfileDetailsContract.Event.Init(userId))
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

            is ProfileDetailsContract.Effect.ExecuteEvent -> {
                onEventSent(uiEffect.event)
            }

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
                val share = Intent.createChooser(Intent().apply {
                    action = Intent.ACTION_SEND
//                    addCategory(Intent.CATEGORY_DEFAULT)
//                    addCategory(Intent.CATEGORY_APP_MESSAGING)
//                    addCategory(Intent.CATEGORY_BROWSABLE)
                    putExtra(Intent.EXTRA_TEXT, uiEffect.url)
                    putExtra(Intent.EXTRA_TITLE, uiEffect.intentTitle)
                    type = "text/plain"
                }, uiEffect.intentTitle)
                context.startActivity(share)
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
            errorMessage = alertDialogData.errorSubtitle,
            dismissButton = alertDialogData.dismissAlertDialogButton,
            confirmButton = alertDialogData.confirmAlertDialogButton
        )
    }

    WrapModalBottomSheetLayout(
        sheetState = modalBottomSheetState,
        sheetContent = {
            when (val bottomSheetType = state.bottomSheetType) {
                ProfileDetailsBottomSheetType.MoreActions -> {
                    BottomSheetMoreActions(
                        moreActionItems = state.moreActionItems,
                        onEventSent = { onEventSent(it) }
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
            navigableAction = ScreenNavigableAction.Backable(onBack = {
                onEventSent(
                    ProfileDetailsContract.Event.OnBackClicked
                )
            }),
            showToolbar = true,
            toolbarTitle = state.profileDetails?.basicProfileDetails?.fullName,
            toolbarActions = when (state.isDifferentUser) {
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
                        order = 0,
                        iconData = IconData(
                            iconResource = R.drawable.ic_more,
                            contentDescription = R.string.content_description_ic_more_actions
                        ),
                        onClick = { onEventSent(ProfileDetailsContract.Event.MoreActions.OnClicked) }
                    )
                )
            },
            snackbarStateData = state.snackbarStateData,
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
            ) {
                // User does not exist or is blocked error view.
                state.nestedErrorView?.let { nestedErrorView ->
                    NestedErrorView(
                        nestedErrorView = nestedErrorView,
                        onEventSent = { onEventSent(it) }
                    )
                }

                // Profile details.
                state.profileDetails?.let { profileDetails ->
                    // Profile Banner.
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .drawBehind {
                                drawOutline(
                                    outline = bannerShape.createOutline(
                                        size = Size(width = size.width, height = size.height),
                                        layoutDirection = layoutDirection,
                                        density = this
                                    ),
                                    color = bannerShapeColor,
                                    style = Fill
                                )
                            }
                            .padding(ConstantsUi.ProfileDetailsScreen.PROFILE_BANNER_PADDING)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Profile image.
                            WrapAsyncImage(
                                imageUrl = profileDetails.basicProfileDetails.profileImageUrl,
                                contentDescription = stringResource(R.string.content_description_your_profile_image),
                                modifier = Modifier
                                    .size(ConstantsUi.PROFILE_IMAGE_SMALL_SIZE)
                                    .circularImageWithBorder(
                                        borderWidth = ConstantsUi.IMAGE_BORDER_WIDTH,
                                        borderColor = MaterialTheme.colors.error
                                    ),
                                error = painterResource(R.drawable.ic_person),
                                placeholder = painterResource(R.drawable.ic_person),
                                contentScale = ContentScale.Fit
                            )

                            // Profile Banner tiles.
                            Layout(
                                content = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(32.dp)
                                    ) {
                                        // Friends tile.
                                        ProfileBannerTile(
                                            label = stringResource(R.string.profile_details_screen_label_profile_banner_tile_friends),
                                            count = profileDetails.userStats.friendsCount,
                                            onClick = { onEventSent(ProfileDetailsContract.Event.ProfileBanner.OnFriendsListClicked) }
                                        )

                                        ProfileBannerTile(
                                            label = stringResource(R.string.profile_details_screen_label_profile_banner_tile_listings),
                                            count = profileDetails.userStats.listingsCount,
                                            onClick = { onEventSent(ProfileDetailsContract.Event.ProfileBanner.OnListingsListClicked) }
                                        )

                                        ProfileBannerTile(
                                            label = stringResource(R.string.profile_details_screen_label_profile_banner_tile_favorites),
                                            count = profileDetails.userStats.favoritesCount,
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

                        VSpacer.Medium()
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
                                        VisibleField(label = stringResource(profileDetails.basicProfileDetails.gender.label))
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
                                        VisibleField(label = profileDetails.basicProfileDetails.email)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    BackHandler(modalBottomSheetState.isVisible) {
        onEventSent(ProfileDetailsContract.Event.OnSystemBack)
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
private fun VisibleField(label: String) {
    Text(
        text = label,
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
    nestedErrorView: ProfileDetailsNestedErrorView,
    onEventSent: (ProfileDetailsContract.Event) -> Unit
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

        VSpacer.Medium()

        WrapButton(onClick = { onEventSent(nestedErrorView.onButtonClicked) }) {
            Text(text = nestedErrorView.buttonLabel)
        }
    }
}

@Preview(
    apiLevel = 34,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun ProfileDetailsScreenPreview() {
    MarvelXTheme {
        ProfileDetailsScreen(
            onEventSent = {},
            state = ProfileDetailsContract.State(
                isLoading = false,
                profileDetails = ProfileDetailsDomain.mockAnotherUser(),
                isEmailHidden = true
            ),
            effect = emptyFlow(),
            userId = -1,
            onNavigation = {}
        )
    }
}

@Preview(
    apiLevel = 34,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
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
                    errorResourceId = R.raw.lottie_not_available,
                    buttonLabel = stringResource(R.string.profile_details_screen_label_full_screen_error_view_button_go_back),
                    onButtonClicked = ProfileDetailsContract.Event.OnBackClicked
                )
            ),
            effect = emptyFlow(),
            userId = -1,
            onNavigation = {}
        )
    }
}

@Preview(
    apiLevel = 34,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
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
                        titleColor = MaterialTheme.customColors.error,
                        onClick = ProfileDetailsContract.Event.MoreActions.OnBlockClicked
                    ),
                    BottomSheetContentItem(
                        title = stringResource(R.string.profile_details_screen_label_more_actions_share_profile),
                        onClick = ProfileDetailsContract.Event.MoreActions.OnShareProfileClicked
                    ),
                    BottomSheetContentItem(
                        title = stringResource(R.string.profile_details_screen_label_more_actions_show_qr_code),
                        onClick = ProfileDetailsContract.Event.MoreActions.OnShowQrCodeClicked
                    ),
                ),
                bottomSheetType = ProfileDetailsBottomSheetType.MoreActions
            ),
            effect = emptyFlow(),
            userId = -1,
            onNavigation = {}
        )
    }
}
