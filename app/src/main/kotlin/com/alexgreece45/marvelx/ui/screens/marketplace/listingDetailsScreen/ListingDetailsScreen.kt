package com.alexgreece45.marvelx.ui.screens.marketplace.listingDetailsScreen

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.core.common.extensions.toListingFormat
import com.alexgreece45.marvelx.data.model.marketplace.listing.EnumGeneric
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.ListingDetailsDomain
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.ListingDimensions
import com.alexgreece45.marvelx.domain.model_api.user.privacySettings.PrivacySettingsDomain
import com.alexgreece45.marvelx.domain.model_api.user.profileDetails.BasicProfileDetailsDomain
import com.alexgreece45.marvelx.domain.model_api.user.profileDetails.ProfileDetailsDomain
import com.alexgreece45.marvelx.domain.model_api.user.profileDetails.RelationshipStatusDomain
import com.alexgreece45.marvelx.domain.model_api.user.profileDetails.UserStatsDomain
import com.alexgreece45.marvelx.domain.model_generic.ReloadableImageUrlData
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.GenericErrorScreen
import com.alexgreece45.marvelx.ui.components.IconData
import com.alexgreece45.marvelx.ui.components.InfoBox
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.ToolbarAction
import com.alexgreece45.marvelx.ui.components.WrapAsyncImage
import com.alexgreece45.marvelx.ui.components.WrapButton
import com.alexgreece45.marvelx.ui.components.WrapHorizontalPager
import com.alexgreece45.marvelx.ui.components.effect.ObserveUiEffect
import com.alexgreece45.marvelx.ui.components.icons.ProcessingRequestIconLottie
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
import com.alexgreece45.marvelx.ui.theme.MarvelXTheme
import com.alexgreece45.marvelx.ui.theme.values.customColors
import com.alexgreece45.marvelx.util.DateUtil
import com.alexgreece45.marvelx.util.SPACING_SMALL
import com.alexgreece45.marvelx.util.SPACING_TINY
import com.alexgreece45.marvelx.util.VSpacer
import com.alexgreece45.marvelx.util.constants.ConstantsUi
import com.alexgreece45.marvelx.util.extensions.circularImageWithBorder
import com.alexgreece45.marvelx.util.extensions.clickableNoRipple
import com.alexgreece45.marvelx.util.extensions.formatPrice
import com.alexgreece45.marvelx.util.extensions.shareContentUrl
import com.alexgreece45.marvelx.util.extensions.showToast
import com.alexgreece45.marvelx.util.safeLetComposable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun ListingDetailsScreen(
    onEvent: (ListingDetailsContract.Event) -> Unit,
    state: ListingDetailsContract.State,
    effect: Flow<ListingDetailsContract.Effect>,
    onNavigation: (ListingDetailsContract.Effect.Navigation) -> Unit,
    listingId: String?,
    createListingWizardMode: CreateListingWizardMode?
) {

    val context = LocalContext.current

    ObserveUiEffect(effect) { uiEffect ->
        when (uiEffect) {
            is ListingDetailsContract.Effect.Navigation -> onNavigation(uiEffect)
            is ListingDetailsContract.Effect.ShowToastMessage -> {
                context.showToast(uiEffect.message)
            }

            is ListingDetailsContract.Effect.ShareListingUrl -> {
                context.shareContentUrl(url = uiEffect.url, title = uiEffect.title)
            }
        }
    }

    LaunchedEffect(Unit) {
        onEvent(ListingDetailsContract.Event.Init(listingId, createListingWizardMode))
    }

    ContentScreen(
        isLoading = state.isLoading,
        navigableAction = ScreenNavigableAction.Backable(
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
        toolbarActions = getToolbarActions(
            canEditListing = state.listing?.canEdit,
            isListingFavorite = state.listing?.favorite,
            mode = state.mode,
            onToolbarActionButtonEvent = onEvent
        ),
        stickyBottom = {
            safeLetComposable(state.listing, state.mode) { listing, mode ->
                if (mode == ListingDetailsMode.EXISTING
                    && !listing.canEdit
                ) {
                    StickyBottom(
                        listingStatus = listing.status,
                        buttonLoading = state.addToBasketButtonLoading,
                        onButtonClicked = { onEvent(ListingDetailsContract.Event.OnStickyBottomButtonClicked) }
                    )
                }
            }
        }
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
                    onErrorButtonClicked = { onEvent(ListingDetailsContract.Event.OnErrorRetryButtonClicked) }
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
                    onCarouselScrolled = {
                        onEvent(ListingDetailsContract.Event.OnCarouselScrolled(it))
                    },
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
    onCarouselScrolled: (Int) -> Unit,
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
                    .height(150.dp)
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
            formattedDate = listing.updatedAt.label
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
        modifier = Modifier.clickableNoRipple(onClick = onClick),
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
                )
        )

        Text(
            text = fullName,
            style = MaterialTheme.typography.subtitle1
        )
    }
}

/**
 * Sub sections
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
            contentDescription = R.string.screen_listing_details_content_description_apparel_size
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
            collectibleDetailsDomain.scale
        ),
        iconData = IconData(
            iconResource = R.drawable.ic_listing_collectible_scale,
            contentDescription = R.string.screen_listing_details_listing_details_label_collectible_scale
        )
    )

    DetailsSubSectionItem(
        label = stringResource(
            R.string.screen_listing_details_listing_details_label_box_condition,
            collectibleDetailsDomain.boxCondition
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
            contentDescription = R.string.screen_listing_details_content_description_person
        )
    )

    DetailsSubSectionItem( // TODO: Add description. -- > informative box through a tooltip
        label = comicDetailsDomain.year.toString(),
        iconData = IconData(
            iconResource = R.drawable.ic_listing_year,
            contentDescription = R.string.screen_listing_details_content_description_calendar
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
    canEditListing: Boolean?,
    isListingFavorite: Boolean?,
    mode: ListingDetailsMode?,
    onToolbarActionButtonEvent: (ListingDetailsContract.Event.ToolbarActionButton) -> Unit
): List<ToolbarAction>? {
    if (canEditListing == null
        || isListingFavorite == null
        || mode == null
        || mode == ListingDetailsMode.OVERVIEW
    ) return null

    return listOf(
        if (canEditListing) {
            ToolbarAction(
                label = stringResource(R.string.screen_listing_details_toolbar_action_label_edit_listing),
                order = 1,
                iconData = IconData(
                    iconResource = R.drawable.ic_edit,
                    contentDescription = R.string.screen_listing_details_toolbar_action_label_edit_listing
                ),
                onClick = { onToolbarActionButtonEvent(ListingDetailsContract.Event.ToolbarActionButton.OnEditClicked) }
            )
        } else {
            if (isListingFavorite) {
                ToolbarAction(
                    label = stringResource(R.string.screen_listing_details_toolbar_action_label_unfavorite_listing),
                    order = 1,
                    iconData = IconData(
                        iconResource = R.drawable.ic_favorite_filled,
                        contentDescription = R.string.screen_listing_details_toolbar_action_label_unfavorite_listing,
                        tint = MaterialTheme.customColors.genericRed
                    ),
                    onClick = { onToolbarActionButtonEvent(ListingDetailsContract.Event.ToolbarActionButton.OnFavoriteClicked) }
                )
            } else {
                ToolbarAction(
                    label = stringResource(R.string.screen_listing_details_toolbar_action_label_favorite_listing),
                    order = 1,
                    iconData = IconData(
                        iconResource = R.drawable.ic_favorite_empty,
                        contentDescription = R.string.screen_listing_details_toolbar_action_label_favorite_listing
                    ),
                    onClick = { onToolbarActionButtonEvent(ListingDetailsContract.Event.ToolbarActionButton.OnFavoriteClicked) }
                )
            }
        },
        ToolbarAction(
            label = stringResource(R.string.screen_listing_details_toolbar_action_label_share_listing),
            order = 2,
            iconData = IconData(
                iconResource = R.drawable.ic_share,
                contentDescription = R.string.screen_listing_details_toolbar_action_label_share_listing
            ),
            onClick = { onToolbarActionButtonEvent(ListingDetailsContract.Event.ToolbarActionButton.OnShareClicked) }
        )
    )
}

@Composable
private fun StickyBottom(
    listingStatus: ListingStatusUi,
    buttonLoading: Boolean,
    onButtonClicked: () -> Unit
) {
    when (listingStatus) {
        ListingStatusUi.Draft -> {}

        ListingStatusUi.Active -> {
            WrapButton(
                onClick = onButtonClicked,
                modifier = Modifier.fillMaxWidth(),
                enabled = !buttonLoading,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.customColors.genericRed,
                    contentColor = MaterialTheme.customColors.buttonContentWhite,

                    // We use the same colors as the enabled state in order to highlight the loading
                    // icon.
                    disabledBackgroundColor = MaterialTheme.customColors.genericRed,
                    disabledContentColor = MaterialTheme.customColors.buttonContentWhite
                )
            ) {
                if (buttonLoading) {
                    // TODO: Review icon.
//                    CircularProgressIndicator(
//                        modifier = Modifier.size(ConstantsUi.MIN_ICON_SIZE),
//                        color = MaterialTheme.customColors.white
//                    )
                    ProcessingRequestIconLottie()
                } else {
                    Text(text = stringResource(R.string.screen_listing_details_sticky_bottom_button_add_to_basket))
                }
            }
        }

        ListingStatusUi.Reserved -> {
            InfoBox(
                backgroundColor = Color(0xA6D57500),
                outlineColor = Color(0xFFD57500),
                label = stringResource(R.string.screen_listing_details_sticky_bottom_label_reserved_listing),
                iconData = IconData(
                    iconResource = R.drawable.ic_error,
                    contentDescription = R.string.screen_listing_details_content_description_info_icon
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        ListingStatusUi.Sold -> {}
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
private fun ListingDetailsScreenPreview() {
    val price = "150.20".toBigDecimal()
    val postedAt = DateUtil.getUTCInstant().minusSeconds(60 * 10)
    MarvelXTheme {
        ListingDetailsScreen(
            onEvent = { },
            state = ListingDetailsContract.State(
                isLoading = false,
                mode = ListingDetailsMode.EXISTING,
                listingId = "1",
                listing = ListingUi(
                    listingId = "1",
                    sellerUserId = "23223",
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
                    title = "Rare 2005 Spider-Man Limited Edition Mug",
                    description = "Exclusive 2005 edition Spider-Man mug. It is of high quality.",
                    price = BigDecimalUi(
                        value = price,
                        label = price.formatPrice(2)
                    ),
                    coverImageUrl = "https://backend.alexandrosvoliotis.com/api/private/marvelx/user/1/profile-image",
                    imageUrls = listOf("https://backend.alexandrosvoliotis.com/api/private/marvelx/user/2/profile-image"),
                    selectedImageIndex = 0,
//                    details = ListingDetailsDomain.DrinkwareDetailsDomain(
//                        material = ListingEnumGeneric(
//                            "ceramic",
//                            "Ceramic"
//                        ),
//                        capacityMl = 500,
//                        capacityMlLabel = "500Ml",
//                        dishwasherSafe = true
//                    ),
//                    details = ListingDetailsDomain.CardDetailsDomain(
//                        grade = ListingEnumGeneric(
//                            code = "very_good",
//                            label = "Very Good"
//                        ),
//                        rarity = ListingEnumGeneric(
//                            code = "ultra_rare",
//                            label = "Ultra Rare"
//                        ),
//                        variant = ListingEnumGeneric(
//                            code = "holo",
//                            label = "Holographic"
//                        )
//                    ),
                    details = ListingDetailsDomain.ArtDetailsDomain(
                        dimensions = ListingDimensions(
                            width = WizardInputFieldUi.Integer(integerValue = "10"),
                            height = WizardInputFieldUi.Integer(integerValue = "20"),
                        ),
                        framed = true
                    ),
                    favorite = false,
                    cart = false,
                    postedAt = InstantUi(
                        value = postedAt,
                        label = postedAt.toListingFormat()
                    ),
                    updatedAt = InstantUi(
                        value = null,
                        label = "-"
                    ),
                    canEdit = false
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
                )
            ),
            effect = emptyFlow(),
            onNavigation = {},
            listingId = "1",
            createListingWizardMode = null
        )
    }
}
