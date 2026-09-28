package com.alexgreece45.marvelx.ui.screens.marketplace.listingDetailsScreen

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.common_ui.components.WrapClickableText
import com.alexgreece45.marvelx.common_ui.components.model.ClickableString
import com.alexgreece45.marvelx.common_ui.components.statusContainer.StatusContainer
import com.alexgreece45.marvelx.common_ui.extensions.clickableNoRipple
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarEvent
import com.alexgreece45.marvelx.common_ui.util.ObserveUiEffect
import com.alexgreece45.marvelx.common_ui.util.safeLetComposable
import com.alexgreece45.marvelx.core.common.extensions.formatPrice
import com.alexgreece45.marvelx.core.common.extensions.minusMinutes
import com.alexgreece45.marvelx.core.common.extensions.toListingFormat
import com.alexgreece45.marvelx.core.common.util.flows.single_observer_flow.SingleObserverFlow
import com.alexgreece45.marvelx.data.model.marketplace.listing.EnumGeneric
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.ListingDetailsDomain
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.ListingDimensions
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.ListingOrderIdsDomain
import com.alexgreece45.marvelx.domain.model_api.order.OrderIdDomain
import com.alexgreece45.marvelx.domain.model_api.user.privacySettings.PrivacySettingsDomain
import com.alexgreece45.marvelx.domain.model_api.user.profileDetails.BasicProfileDetailsDomain
import com.alexgreece45.marvelx.domain.model_api.user.profileDetails.ProfileDetailsDomain
import com.alexgreece45.marvelx.domain.model_api.user.profileDetails.RelationshipStatusDomain
import com.alexgreece45.marvelx.domain.model_api.user.profileDetails.UserStatsDomain
import com.alexgreece45.marvelx.domain.model_generic.ReloadableImageUrlData
import com.alexgreece45.marvelx.model.model.user.UserId
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.GenericErrorScreen
import com.alexgreece45.marvelx.ui.components.IconData
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.ToolbarAction
import com.alexgreece45.marvelx.ui.components.WrapAsyncImage
import com.alexgreece45.marvelx.ui.components.WrapButton
import com.alexgreece45.marvelx.ui.components.WrapHorizontalPager
import com.alexgreece45.marvelx.ui.model.Gender
import com.alexgreece45.marvelx.ui.model_generic.BigDecimalUi
import com.alexgreece45.marvelx.ui.model_generic.InstantUi
import com.alexgreece45.marvelx.ui.screens.marketplace.components.ListingImage
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.CreateListingWizardMode
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.WizardInputFieldUi
import com.alexgreece45.marvelx.ui.screens.marketplace.listingDetailsScreen.model.ListingDetailsContract
import com.alexgreece45.marvelx.ui.screens.marketplace.listingDetailsScreen.model.ListingDetailsMode
import com.alexgreece45.marvelx.ui.screens.marketplace.model.ListingStatusUi
import com.alexgreece45.marvelx.ui.screens.marketplace.model.ListingUi
import com.alexgreece45.marvelx.ui.screens.orders.model.item.OrderItemStatusUi
import com.alexgreece45.marvelx.ui.screens.orders.model.item.OrderItemUi
import com.alexgreece45.marvelx.ui_theme.theme.MarvelXTheme
import com.alexgreece45.marvelx.ui_theme.theme.import_aliases.UiThemeDrawable
import com.alexgreece45.marvelx.ui_theme.theme.values.customColors
import com.alexgreece45.marvelx.util.DateUtil
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.SPACING_TINY
import com.alexgreece45.marvelx.util.VSpacer
import com.alexgreece45.marvelx.util.constants.ConstantsUi
import com.alexgreece45.marvelx.util.extensions.circularImageWithBorder
import com.alexgreece45.marvelx.util.extensions.shareContentUrl
import com.alexgreece45.marvelx.util.extensions.showToast
import kotlinx.coroutines.flow.emptyFlow

@Composable
internal fun ListingDetailsScreen(
    onEvent: (ListingDetailsContract.Event) -> Unit,
    state: ListingDetailsContract.State,
    effect: SingleObserverFlow<ListingDetailsContract.Effect>,
    snackbarEffects: SingleObserverFlow<SnackbarEvent>,
    onNavigation: (ListingDetailsContract.Effect.Navigation) -> Unit,
    listingId: String?,
    createListingWizardMode: CreateListingWizardMode?,
    isInnerScreen: Boolean
) {
    val context = LocalContext.current

    ObserveUiEffect(flow = effect, tag = "ListingDetailsScreen") { uiEffect ->
        when (uiEffect) {
            is ListingDetailsContract.Effect.Navigation -> onNavigation(uiEffect)
            is ListingDetailsContract.Effect.ShowToastMessage -> {
                context.showToast(uiEffect.message)
            }

            is ListingDetailsContract.Effect.ShareListingUrl -> {
                context.shareContentUrl(shareContentUrlData = uiEffect.shareContentUrlData)
            }
        }
    }

    LaunchedEffect(Unit) {
        onEvent(ListingDetailsContract.Event.Init(listingId, createListingWizardMode))
    }

    ContentScreen(
        isLoading = state.isLoading,
        navigableAction = ScreenNavigableAction.Backable(
            testTag = ListingDetailsTestTags.BACK_BUTTON,
            onBack = {
                onEvent(
                    ListingDetailsContract.Event.OnBack
                )
            }
        ),
        onBack = {
            onEvent(
                ListingDetailsContract.Event.OnBack
            )
        },
        showToolbar = state.showToolbar,
        toolbarTitle = state.toolbarTitle,
        toolbarActions = if (state.mode == null || state.mode == ListingDetailsMode.OVERVIEW || state.listing?.status != ListingStatusUi.Active) null else {
            with(state.listing) {
                getToolbarActions(
                    listingStatus = status,
                    canEditListing = canEdit,
                    isListingFavorite = favorite,
                    isFavoriteButtonLoading = state.isFavoriteButtonLoading,
                    onToolbarActionButtonEvent = onEvent
                )
            }
        },
        snackbarEvents = snackbarEffects,
        stickyBottom = {
            safeLetComposable(state.listing, state.mode) { listing, mode ->
                if (mode == ListingDetailsMode.EXISTING) {
                    StickyBottomButton(
                        listingStatus = listing.status,
                        isListingInCart = listing.cart,
                        canEdit = listing.canEdit,
                        buttonLoading = state.stickyBottomButtonLoading,
                        onClick = { onEvent(ListingDetailsContract.Event.OnStickyBottomButtonClicked) }
                    )
                }
            }
        },
        isInnerScreen = isInnerScreen
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
        ) {
            if (state.hasError) {
                GenericErrorScreen(
                    errorMessage = stringResource(R.string.screen_listing_details_error_loading_listing),
                    modifier = Modifier.fillMaxSize(),
                    buttonTestTag = ListingDetailsTestTags.ERROR_RETRY_BUTTON,
                    onErrorButtonClick = { onEvent(ListingDetailsContract.Event.OnErrorRetryButtonClicked) }
                )
                return@ContentScreen
            }

            safeLetComposable(
                state.listing,
                state.sellerProfileDetails
            ) { listing, sellerProfileDetails ->
                ListingView(
                    listing = listing,
                    sellerProfileDetails = sellerProfileDetails,
                    orderItem = state.listingOrderItem,
                    onCarouselScrolled = {
                        onEvent(ListingDetailsContract.Event.OnCarouselScrolled(it))
                    },
                    onOrderItemStatusContainerClickableTextClick = { onEvent(ListingDetailsContract.Event.OnOrderItemStatusContainerClickableTextClicked) },
                    onSellerInfoSectionClicked = {
                        onEvent(ListingDetailsContract.Event.OnSellerInfoClicked)
                    }
                )
            }
        }
    }
}

@Composable
private fun ListingView(
    listing: ListingUi,
    sellerProfileDetails: ProfileDetailsDomain,
    orderItem: OrderItemUi?,
    onCarouselScrolled: (Int) -> Unit,
    onOrderItemStatusContainerClickableTextClick: () -> Unit,
    onSellerInfoSectionClicked: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        WrapHorizontalPager(
            pageCount = listing.allImageUrls.size,
            selectedIndex = listing.selectedImageIndex,
            onPageChanged = onCarouselScrolled
        ) { pageIndex ->
            ListingImage(
                imageUrl = listing.allImageUrls.getOrElse(pageIndex) { "" },
                title = listing.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Fit
            )
        }

        VSpacer.Medium()

        Text(
            text = listing.title,
            style = MaterialTheme.typography.h4
        )

        VSpacer.Small()

        Text(
            text = listing.price.label,
            color = MaterialTheme.customColors.genericRed,
            style = MaterialTheme.typography.h5
        )

        VSpacer.Medium()

        safeLetComposable(
            p1 = listing.orderIds,
            p2 = orderItem
        ) { orderIds, _ ->
            StatusContainer(
                title = stringResource(if (listing.canEdit) R.string.screen_listing_details_seller_status_container_title else R.string.screen_listing_details_buyer_status_container_title),
                titleColor = MaterialTheme.customColors.successStatusContainerColors.contentColor,
                backgroundColor = MaterialTheme.customColors.successStatusContainerColors.backgroundColor,
                modifier = Modifier.fillMaxWidth(),
                outlineColor = MaterialTheme.customColors.successStatusContainerColors.outlineColor,
                icon = {
                    Box(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.customColors.successStatusContainerColors.outlineColor,
                                shape = CircleShape
                            )
                            .clip(CircleShape)
                            .padding(SPACING_TINY.dp)
                            .size(28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(UiThemeDrawable.ic_tag),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.customColors.successStatusContainerColors.contentColor
                        )
                    }
                },
                extraContent = {
                    val statusLabel = stringResource(listing.status.labelResId)
                    WrapClickableText(
                        text = if (listing.canEdit) stringResource(
                            R.string.screen_listing_details_seller_status_container_body,
                            orderIds.orderId.formatted,
                            statusLabel
                        ) else stringResource(
                            R.string.screen_listing_details_buyer_status_container_body,
                            orderIds.orderId.formatted,
                            statusLabel
                        ),
                        textStyle = LocalTextStyle.current,
                        modifier = Modifier.testTag(ListingDetailsTestTags.ORDER_ITEM_STATUS_LINK),
                        clickableStrings = listOf(
                            ClickableString(
                                text = stringResource(if (listing.canEdit) R.string.screen_listing_details_seller_status_container_body_clickable else R.string.screen_listing_details_buyer_status_container_body_clickable),
                                color = MaterialTheme.customColors.linkTextColor,
                                onClick = { onOrderItemStatusContainerClickableTextClick() }
                            )
                        )
                    )
                }
            )
        }

        VSpacer.Medium()

        Text(
            text = stringResource(R.string.screen_listing_details_listing_description),
            style = MaterialTheme.typography.subtitle1
        )

        Text(
            text = listing.description,
            style = MaterialTheme.typography.body1
        )

        VSpacer.Medium()

        ListingDetailsSection(listing.details)

        VSpacer.Medium()

        DateSection(
            label = stringResource(R.string.screen_listing_details_listing_posted_at),
            formattedDate = listing.postedAt.label
        )

        VSpacer.Small()

        DateSection(
            label = stringResource(R.string.screen_listing_details_listing_updated_at),
            formattedDate = listing.updatedAt?.label ?: "-"
        )

        VSpacer.Medium()

        SellerInfoSection(
            profileImageUrl = sellerProfileDetails.basicProfileDetails.profileImageUrlData.imageUrl,
            fullName = sellerProfileDetails.basicProfileDetails.fullName,
            onClick = onSellerInfoSectionClicked
        )
    }
}

@Composable
private fun DateSection(
    label: String,
    formattedDate: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(SPACING_TINY.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.caption
        )

        Text(
            text = formattedDate,
            style = MaterialTheme.typography.subtitle2
        )
    }
}

@Composable
private fun ListingDetailsSection(listingDetails: ListingDetailsDomain) {
    Column(
        verticalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)
    ) {
        when (listingDetails) {
            is ListingDetailsDomain.ApparelDetailsDomain -> ApparelDetailsSubSection(listingDetails)
            is ListingDetailsDomain.DrinkwareDetailsDomain -> DrinkwareDetailsSubSection(
                listingDetails
            )

            is ListingDetailsDomain.CollectibleDetailsDomain -> CollectibleDetailsSubSection(
                listingDetails
            )

            is ListingDetailsDomain.ArtDetailsDomain -> ArtDetailsSubSection(listingDetails)
            is ListingDetailsDomain.AccessoryDetailsDomain -> AccessoryDetailsSubSection(
                listingDetails
            )

            is ListingDetailsDomain.HomeDetailsDomain -> HomeDetailsSubSection(listingDetails)
            is ListingDetailsDomain.ComicDetailsDomain -> ComicDetailsSubSection(listingDetails)
            is ListingDetailsDomain.CardDetailsDomain -> CardDetailsSubSection(listingDetails)
        }
    }
}

@Composable
private fun SellerInfoSection(profileImageUrl: String, fullName: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .testTag(ListingDetailsTestTags.SELLER_INFO_SECTION)
            .clickableNoRipple(onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        WrapAsyncImage(
            imageUrl = profileImageUrl,
            contentDescription = stringResource(R.string.screen_listing_details_content_description_seller_profile_image),
            modifier = Modifier
                .size(50.dp)
                .circularImageWithBorder(
                    borderWidth = ConstantsUi.IMAGE_BORDER_WIDTH,
                    borderColor = MaterialTheme.customColors.genericRed
                ),
            contentScale = ContentScale.Crop
        )

        Text(
            text = fullName,
            style = MaterialTheme.typography.subtitle1
        )
    }
}

/**
 * Sub-sections
 */

/**
 * Details subsection item.
 */
@Composable
private fun DetailsSubSectionItem(
    label: String,
    iconData: IconData
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(SPACING_TINY.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(iconData.iconResource),
            contentDescription = stringResource(iconData.contentDescription),
            modifier = Modifier.size(24.dp),
            colorFilter = iconData.tint?.let { ColorFilter.tint(it) }
        )

        Text(
            text = label,
            style = MaterialTheme.typography.subtitle2
        )
    }
}

/**
 * Composable for displaying dimensions; width and height.
 */
@Composable
private fun ColumnScope.DimensionDetailsSubSection(listingDimensions: ListingDimensions) {
    DetailsSubSectionItem(
        label = stringResource(
            R.string.screen_listing_details_listing_details_label_dimensions,
            listingDimensions.width.integerValue,
            listingDimensions.height.integerValue
        ),
        iconData = IconData(
            iconResource = R.drawable.ic_listing_size,
            contentDescription = R.string.screen_listing_details_content_description_dimensions
        )
    )
}

@Composable
private fun ColumnScope.ApparelDetailsSubSection(listingDetails: ListingDetailsDomain.ApparelDetailsDomain) {
    DetailsSubSectionItem(
        label = stringResource(
            R.string.screen_listing_details_listing_details_label_apparel_size,
            listingDetails.size.label
        ),
        iconData = IconData(
            iconResource = R.drawable.ic_listing_size,
            contentDescription = R.string.screen_listing_details_content_description_apparel_size,
            tint = MaterialTheme.customColors.white
        )
    )

    DetailsSubSectionItem(
        label = stringResource(
            R.string.screen_listing_details_listing_details_label_color,
            listingDetails.color
        ),
        iconData = IconData(
            iconResource = R.drawable.ic_listing_color_palette,
            contentDescription = R.string.screen_listing_details_content_description_color_palette
        )
    )
}

@Composable
private fun ColumnScope.DrinkwareDetailsSubSection(drinkwareDetailsDomain: ListingDetailsDomain.DrinkwareDetailsDomain) {
    DetailsSubSectionItem(
        label = stringResource(
            R.string.screen_listing_details_listing_details_label_material,
            drinkwareDetailsDomain.material.label
        ),
        iconData = IconData(
            iconResource = R.drawable.ic_listing_material,
            contentDescription = R.string.screen_listing_details_content_description_material
        )
    )

    DetailsSubSectionItem(
        label = stringResource(
            R.string.screen_listing_details_listing_details_label_drinkware_capacity,
            drinkwareDetailsDomain.capacityMl
        ),
        iconData = IconData(
            iconResource = R.drawable.ic_listing_liquid_capacity,
            contentDescription = R.string.screen_listing_details_content_description_liquid_capacity
        )
    )

    if (drinkwareDetailsDomain.dishwasherSafe) {
        DetailsSubSectionItem(
            label = stringResource(R.string.screen_listing_details_listing_details_label_dishwasher_safe),
            iconData = IconData(
                iconResource = R.drawable.ic_check,
                contentDescription = R.string.screen_listing_details_content_description_checkmark,
                tint = MaterialTheme.customColors.greenCheckmark
            )
        )
    } else {
        DetailsSubSectionItem(
            label = stringResource(R.string.screen_listing_details_listing_details_label_not_dishwasher_safe),
            iconData = IconData(
                iconResource = R.drawable.ic_close,
                contentDescription = R.string.screen_listing_details_content_description_not_applicable,
                tint = MaterialTheme.customColors.genericRed
            )
        )
    }
}

@Composable
private fun ColumnScope.CollectibleDetailsSubSection(collectibleDetailsDomain: ListingDetailsDomain.CollectibleDetailsDomain) {
    DetailsSubSectionItem(
        label = stringResource(
            R.string.screen_listing_details_listing_details_label_collectible_scale,
            collectibleDetailsDomain.scale.label
        ),
        iconData = IconData(
            iconResource = R.drawable.ic_listing_collectible_scale,
            contentDescription = R.string.screen_listing_details_listing_details_label_collectible_scale
        )
    )

    DetailsSubSectionItem(
        label = stringResource(
            R.string.screen_listing_details_listing_details_label_box_condition,
            collectibleDetailsDomain.boxCondition.label
        ),
        iconData = IconData(
            iconResource = R.drawable.ic_listing_quality,
            contentDescription = R.string.screen_listing_details_content_description_quality
        )
    )
}

@Composable
private fun ColumnScope.ArtDetailsSubSection(artDetailsDomain: ListingDetailsDomain.ArtDetailsDomain) {
    DimensionDetailsSubSection(listingDimensions = artDetailsDomain.dimensions)

    if (artDetailsDomain.framed) {
        DetailsSubSectionItem(
            label = stringResource(R.string.screen_listing_details_listing_details_label_framed),
            iconData = IconData(
                iconResource = R.drawable.ic_check,
                contentDescription = R.string.screen_listing_details_content_description_checkmark,
                tint = MaterialTheme.customColors.greenCheckmark
            )
        )
    } else {
        DetailsSubSectionItem(
            label = stringResource(R.string.screen_listing_details_listing_details_label_not_framed),
            iconData = IconData(
                iconResource = R.drawable.ic_close,
                contentDescription = R.string.screen_listing_details_content_description_not_applicable,
                tint = MaterialTheme.customColors.genericRed
            )
        )
    }
}

@Composable
private fun ColumnScope.AccessoryDetailsSubSection(accessoryDetailsDomain: ListingDetailsDomain.AccessoryDetailsDomain) {
    DetailsSubSectionItem(
        label = stringResource(
            R.string.screen_listing_details_listing_details_label_material,
            accessoryDetailsDomain.material.label
        ),
        iconData = IconData(
            iconResource = R.drawable.ic_listing_material,
            contentDescription = R.string.screen_listing_details_content_description_material
        )
    )

    DetailsSubSectionItem(
        label = stringResource(
            R.string.screen_listing_details_listing_details_label_color,
            accessoryDetailsDomain.color
        ),
        iconData = IconData(
            iconResource = R.drawable.ic_listing_color_palette,
            contentDescription = R.string.screen_listing_details_content_description_color_palette
        )
    )
}

@Composable
private fun ColumnScope.HomeDetailsSubSection(homeDetailsDomain: ListingDetailsDomain.HomeDetailsDomain) {
    DimensionDetailsSubSection(homeDetailsDomain.dimensions)
}

@Composable
private fun ColumnScope.ComicDetailsSubSection(comicDetailsDomain: ListingDetailsDomain.ComicDetailsDomain) {
    DetailsSubSectionItem(
        label = comicDetailsDomain.grade.label,
        iconData = IconData(
            iconResource = R.drawable.ic_listing_grade,
            contentDescription = R.string.screen_listing_details_content_description_grade
        )
    )

    DetailsSubSectionItem(
        label = comicDetailsDomain.publisher,
        iconData = IconData(
            iconResource = R.drawable.ic_person,
            contentDescription = R.string.screen_listing_details_content_description_person,
            tint = MaterialTheme.customColors.white
        )
    )

    DetailsSubSectionItem( // TODO: Add description. -- > informative box through a tooltip
        label = comicDetailsDomain.year.toString(),
        iconData = IconData(
            iconResource = R.drawable.ic_listing_year,
            contentDescription = R.string.screen_listing_details_content_description_calendar,
            tint = MaterialTheme.customColors.white
        )
    )
}

@Composable
private fun ColumnScope.CardDetailsSubSection(cardDetailsDomain: ListingDetailsDomain.CardDetailsDomain) {
    DetailsSubSectionItem(
        label = cardDetailsDomain.grade.label,
        iconData = IconData(
            iconResource = R.drawable.ic_listing_grade,
            contentDescription = R.string.screen_listing_details_content_description_grade
        )
    )

    DetailsSubSectionItem(
        label = cardDetailsDomain.rarity.label,
        iconData = IconData(
            iconResource = R.drawable.ic_listing_rarity,
            contentDescription = R.string.screen_listing_details_content_description_rarity
        )
    )

    DetailsSubSectionItem(
        label = cardDetailsDomain.variant.label,
        iconData = IconData(
            iconResource = R.drawable.ic_listing_variant,
            contentDescription = R.string.screen_listing_details_content_description_variant
        )
    )
}

@Composable
private fun getToolbarActions(
    listingStatus: ListingStatusUi,
    canEditListing: Boolean,
    isListingFavorite: Boolean,
    isFavoriteButtonLoading: Boolean,
    onToolbarActionButtonEvent: (ListingDetailsContract.Event.ToolbarActionButton) -> Unit
): List<ToolbarAction> {
    val isListingActive = listingStatus == ListingStatusUi.Active

    return buildList {
        if (canEditListing) {
            add(
                ToolbarAction(
                    label = stringResource(R.string.screen_listing_details_toolbar_action_label_edit_listing),
                    order = 1,
                    iconData = IconData(
                        iconResource = R.drawable.ic_edit,
                        contentDescription = R.string.screen_listing_details_toolbar_action_label_edit_listing,
                        enabled = isListingActive
                    ),
                    testTag = ListingDetailsTestTags.EDIT_BUTTON,
                    onClick = { onToolbarActionButtonEvent(ListingDetailsContract.Event.ToolbarActionButton.OnEditClicked) }
                )
            )
        } else {
            if (listingStatus != ListingStatusUi.Active) return@buildList

            if (isListingFavorite) {
                add(
                    ToolbarAction(
                        label = stringResource(R.string.screen_listing_details_toolbar_action_label_unfavorite_listing),
                        order = 1,
                        iconData = IconData(
                            iconResource = R.drawable.ic_favorite_filled,
                            contentDescription = R.string.screen_listing_details_toolbar_action_label_unfavorite_listing,
                            tint = MaterialTheme.customColors.genericRed,
                            enabled = !isFavoriteButtonLoading
                        ),
                        testTag = ListingDetailsTestTags.FAVORITE_BUTTON,
                        onClick = { onToolbarActionButtonEvent(ListingDetailsContract.Event.ToolbarActionButton.OnFavoriteClicked) }
                    )
                )
            } else {
                add(
                    ToolbarAction(
                        label = stringResource(R.string.screen_listing_details_toolbar_action_label_favorite_listing),
                        order = 1,
                        iconData = IconData(
                            iconResource = R.drawable.ic_favorite_empty,
                            contentDescription = R.string.screen_listing_details_toolbar_action_label_favorite_listing,
                            enabled = !isFavoriteButtonLoading
                        ),
                        testTag = ListingDetailsTestTags.FAVORITE_BUTTON,
                        onClick = { onToolbarActionButtonEvent(ListingDetailsContract.Event.ToolbarActionButton.OnFavoriteClicked) }
                    )
                )
            }
        }

        add(
            ToolbarAction(
                label = stringResource(R.string.screen_listing_details_toolbar_action_label_share_listing),
                order = 2,
                iconData = IconData(
                    iconResource = R.drawable.ic_share,
                    contentDescription = R.string.screen_listing_details_toolbar_action_label_share_listing,
                    enabled = isListingActive
                ),
                testTag = ListingDetailsTestTags.SHARE_BUTTON,
                onClick = { onToolbarActionButtonEvent(ListingDetailsContract.Event.ToolbarActionButton.OnShareClicked) }
            )
        )
    }
}

@Composable
private fun StickyBottomButton(
    listingStatus: ListingStatusUi,
    isListingInCart: Boolean,
    canEdit: Boolean,
    buttonLoading: Boolean,
    onClick: () -> Unit
) {
    val buttonColors = ButtonDefaults.buttonColors(
        backgroundColor = MaterialTheme.customColors.genericRed,
        contentColor = MaterialTheme.customColors.buttonContentWhite,

        // We use the same colors as the enabled state in order to highlight the loading
        // icon.
        disabledBackgroundColor = MaterialTheme.customColors.genericRed,
        disabledContentColor = MaterialTheme.customColors.buttonContentWhite
    )

    when (listingStatus) {
        ListingStatusUi.Draft -> {}

        ListingStatusUi.Active -> {
            if (canEdit) return
            WrapButton(
                onClick = onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(ListingDetailsTestTags.STICKY_BOTTOM_BUTTON),
                enabled = !buttonLoading,
                colors = buttonColors
            ) {
                if (buttonLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(ConstantsUi.MIN_ICON_SIZE),
                        color = MaterialTheme.customColors.white
                    )
                } else {
                    Text(text = stringResource(if (isListingInCart) R.string.screen_listing_details_sticky_bottom_button_remove_from_cart else R.string.screen_listing_details_sticky_bottom_button_add_to_cart))
                }
            }
        }

        ListingStatusUi.Reserved, ListingStatusUi.Sold -> {
            WrapButton(
                onClick = onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(ListingDetailsTestTags.STICKY_BOTTOM_BUTTON),
                colors = buttonColors
            ) {
                Text(text = stringResource(if (canEdit) R.string.screen_listing_details_sticky_bottom_button_manage_order_item else R.string.screen_listing_details_sticky_bottom_button_view_order_item_details))
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
private fun ListingDetailsScreenPreview() {
    val listingId = "1"
    val title = "Rare 2005 Spider-Man Limited Edition Mug"
    val priceBigDecimal = "150.20".toBigDecimal()
    val price = BigDecimalUi(
        value = priceBigDecimal,
        label = priceBigDecimal.formatPrice(2)
    )
    val postedAt = DateUtil.getUTCInstant().minusMinutes(10)
    val orderId = OrderIdDomain("10")
    val orderItemId = OrderIdDomain("B1")

    MarvelXTheme {
        ListingDetailsScreen(
            onEvent = { },
            state = ListingDetailsContract.State(
                stickyBottomButtonLoading = true,
                isLoading = false,
                mode = ListingDetailsMode.EXISTING,
                listingId = listingId,
                listing = ListingUi(
                    listingId = listingId,
                    sellerUserId = UserId("23223"),
                    category = EnumGeneric(
                        code = "drinkware",
                        label = "Drinkware"
                    ),
                    subcategory = EnumGeneric(
                        code = "mug",
                        label = "Mugs"
                    ),
                    condition = EnumGeneric(
                        code = "very_good",
                        label = "Very Good"
                    ),
                    status = ListingStatusUi.Active,
                    title = title,
                    description = "Exclusive 2005 edition Spider-Man mug. It is of high quality.",
                    price = price,
                    coverImageUrl = "https://backend.alexandrosvoliotis.com/api/private/marvelx/user/1/profile-image",
                    imageUrls = listOf("https://backend.alexandrosvoliotis.com/api/private/marvelx/user/2/profile-image"),
                    details = ListingDetailsDomain.ArtDetailsDomain(
                        dimensions = ListingDimensions(
                            width = WizardInputFieldUi.Integer(integerValue = "10"),
                            height = WizardInputFieldUi.Integer(integerValue = "20"),
                        ),
                        framed = true
                    ),
                    /* details = ListingDetailsDomain.DrinkwareDetailsDomain(
                         material = ListingEnumGeneric(
                             "ceramic",
                             "Ceramic"
                         ),
                         capacityMl = 500,
                         capacityMlLabel = "500Ml",
                         dishwasherSafe = true
                     ),
                     details = ListingDetailsDomain.CardDetailsDomain(
                         grade = ListingEnumGeneric(
                             code = "very_good",
                             label = "Very Good"
                         ),
                         rarity = ListingEnumGeneric(
                             code = "ultra_rare",
                             label = "Ultra Rare"
                         ),
                         variant = ListingEnumGeneric(
                             code = "holo",
                             label = "Holographic"
                         )
                     ),*/
                    postedAt = InstantUi(
                        value = postedAt,
                        label = postedAt.toListingFormat()
                    ),
                    updatedAt = null,
                    favorite = false,
                    cart = true,
                    canEdit = false,
                    orderIds = ListingOrderIdsDomain(
                        orderId = orderId,
                        orderItemId = orderItemId
                    )
                ),
                sellerProfileDetails = ProfileDetailsDomain(
                    basicProfileDetails = BasicProfileDetailsDomain(
                        firstName = "Alexandros",
                        lastName = "Voliotis",
                        gender = Gender.Male,
                        email = "alexv@email.com",
                        profileImageUrlData = ReloadableImageUrlData("https://backend.alexandrosvoliotis.com/api/private/marvelx/user/1/profile-image")
                    ),
                    isDifferentUser = true,
                    relationshipStatus = RelationshipStatusDomain.None,
                    privacySettings = PrivacySettingsDomain(
                        isFriendsListVisible = false,
                        isListingsListVisible = false,
                        isFavoritesListVisible = false,
                        isGenderVisible = false,
                        isEmailVisible = false

                    ),
                    userStats = UserStatsDomain(
                        friendsCount = 5,
                        listingsCount = 1,
                        favoritesCount = 2
                    )
                ),
                listingOrderItem = OrderItemUi(
                    orderItemId = orderItemId,
                    sellerUserId = "2",
                    title = title,
                    coverImageUrl = "https://backend.alexandrosvoliotis.com/api/private/marvelx/user/1/profile-image",
                    status = OrderItemStatusUi.OutForDelivery,
                    price = price,
                )
            ),
            effect = SingleObserverFlow(emptyFlow()),
            snackbarEffects = SingleObserverFlow(emptyFlow()),
            onNavigation = {},
            listingId = "1",
            createListingWizardMode = null,
            isInnerScreen = false
        )
    }
}