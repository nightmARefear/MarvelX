package com.alexgreece45.marvelx.ui_theme.theme.values

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * The custom colors of this theme.
 *
 * @property genderMale The color for the male gender icon.
 * @property genderFemale The color for the female gender icon.
 * @property error The color for errors.
 * @property scrim The scrim color for bottom sheets.
 */
@Immutable
data class CustomColors(
    val genderMale: Color,
    val genderFemale: Color,
    val error: Color,
    val scrim: Color,
    val chipBackgroundColors: ChipBackgroundColors,
    val orderStatusBackgroundColors: OrderStatusBackgroundColors,
    val orderStatusContentColors: OrderStatusContentColors,
    val buttonBackgroundPositive: Color,
    val buttonBackgroundNegative: Color,
    val buttonContentWhite: Color,
    val genericRed: Color,
    val friendsDateColor: Color,
    val white: Color,
    val black: Color,
    val greenCheckmark: Color,
    val transparent: Color,
    val info: Color,
    val cardBrandBackgroundColor: Color,
    val linkTextColor: Color,
    val genericOrangeColor: Color,
    val snackbarBackgroundColors: SnackbarBackgroundColors,
    val listingStatusColors: ListingStatusColors,
    val infoStatusContainerColors: StatusContainerColors,
    val warningStatusContainerColors: StatusContainerColors,
    val errorStatusContainerColors: StatusContainerColors,
    val successStatusContainerColors: StatusContainerColors,
    val creamWhite: Color,
)

/**
 * Contains the background colors of all the custom chips.
 */
@Immutable
data class ChipBackgroundColors(
    val addFriend: Color,
    val friends: Color,
    val cancelRequest: Color,
    val incomingFriendRequest: Color,
    val unfriend: Color,
    val listingDraft: Color,
    val listingActive: Color,
    val listingReserved: Color,
    val listingSold: Color,
    val selected: Color,
)

@Immutable
data class OrderStatusBackgroundColors(
    val processing: Color,
    val partiallyShipped: Color,
    val shipped: Color,
    val partiallyDelivered: Color,
    val delivered: Color
)

@Immutable
data class OrderStatusContentColors(
    val processing: Color,
    val shipped: Color,
    val departedSortingFacility: Color,
    val outForDelivery: Color,
    val delivered: Color
)

@Immutable
data class SnackbarBackgroundColors(
    val info: Color,
    val warning: Color,
    val error: Color,
    val success: Color
)

@Immutable
data class ListingStatusColors(
    val soldBackgroundColor: Color,
    val soldOutlineColor: Color
)

@Immutable
data class StatusContainerColors(
    val backgroundColor: Color,
    val outlineColor: Color,
    val contentColor: Color
)

/**
 * The custom light colors.
 */
internal fun customLightColors() = CustomColors(
    genderMale = Color(0xFF2196F3),
    genderFemale = Color(0xFFE91E63),
    error = Color(0xFFD50000),
    scrim = Color(0xFFF1F1F1),
    chipBackgroundColors = ChipBackgroundColors(
        addFriend = Color(0xFF2196F3),
        friends = Color(0xFF2821F3),
        cancelRequest = Color(0xFF2196F3),
        incomingFriendRequest = Color(0xFF21F33D),
        unfriend = Color(0xFFF1F1F1),
        listingDraft = Color(0xFF1B1314),
        listingActive = Color(0xFF21F33D),
        listingReserved = Color(0xFFD56E00),
        listingSold = Color(0xFFD50000),
        selected = Color(0xFFD50000),
    ),
    orderStatusBackgroundColors = OrderStatusBackgroundColors(
        processing = Color(0xFF2196F3),
        partiallyShipped = Color(0xFFFFB74D),
        shipped = Color(0xFFFF9800),
        partiallyDelivered = Color(0xFF81C784),
        delivered = Color(0xFF4CAF50)
    ),
    orderStatusContentColors = OrderStatusContentColors(
        processing = Color(0xFFE3F2FD),
        shipped = Color(0xFFFFF3E0),
        departedSortingFacility = Color(0xFFFFF3E0),
        outForDelivery = Color(0xFFFFF3E0),
        delivered = Color(0xFFE8F5E9)
    ),
    buttonBackgroundPositive = Color(0xFF21F33D),
    buttonBackgroundNegative = Color(0xFFD50000),
    buttonContentWhite = Color(0xFFF1F1F1),
    genericRed = Color(0xFFD50000),
    friendsDateColor = Color(0xFF2196F3),
    white = Color(0xFFFFFFFF),
    black = Color(0xFF000000),
    greenCheckmark = Color(0xFF21F33D),
    transparent = Color(0x00000000),
    info = Color(0xFF2196F3),
    cardBrandBackgroundColor = Color(0xFF1e40af),
    linkTextColor = Color(0xFF1e40af),
    genericOrangeColor = Color(0xFFD56E00),
    snackbarBackgroundColors = SnackbarBackgroundColors(
        info = Color(0xFF2196F3),
        warning = Color(0xFFFF9800),
        error = Color(0xFFD50000),
        success = Color(0xFF21F33D)
    ),
    listingStatusColors = ListingStatusColors(
        soldBackgroundColor = Color(0xA6B71C1C),
        soldOutlineColor = Color(0xFFB71C1C)
    ),
    infoStatusContainerColors = StatusContainerColors(
        backgroundColor = Color(0x1A3B82F6),
        outlineColor = Color(0x4D3B82F6),
        contentColor = Color(0xFF3B82F6)
    ),
    warningStatusContainerColors = StatusContainerColors(
        backgroundColor = Color(0x1AFF9800),
        outlineColor = Color(0x4DFF9800),
        contentColor = Color(0xFFFF9800)
    ),
    errorStatusContainerColors = StatusContainerColors(
        backgroundColor = Color(0x1AEF4444),
        outlineColor = Color(0x4DEF4444),
        contentColor = Color(0xFFEF4444)
    ),
    successStatusContainerColors = StatusContainerColors(
        backgroundColor = Color(0x1A21F33D),
        outlineColor = Color(0x4D21F33D),
        contentColor = Color(0xFF21F33D)
    ),
    creamWhite = Color(0xFFFFFDD0),
)

/**
 * The custom dark colors.
 */
internal fun customDarkColors() = CustomColors(
    genderMale = Color(0xFF2196F3),
    genderFemale = Color(0xFFE91E63),
    error = Color(0xFFD50000),
    scrim = Color(0xFF484848),
    chipBackgroundColors = ChipBackgroundColors(
        addFriend = Color(0xFF2196F3),
        friends = Color(0xFF2821F3),
        cancelRequest = Color(0xFF2196F3),
        incomingFriendRequest = Color(0xFF21F33D),
        unfriend = Color(0xFFF1F1F1),
        listingDraft = Color(0xFF1B1314),
        listingActive = Color(0xFF21F33D),
        listingReserved = Color(0xFFD56E00),
        listingSold = Color(0xFFD50000),
        selected = Color(0xFFD50000),
    ),
    orderStatusBackgroundColors = OrderStatusBackgroundColors(
        processing = Color(0xFF2196F3),
        partiallyShipped = Color(0xFFFFB74D),
        shipped = Color(0xFFD56E00),
        partiallyDelivered = Color(0xFF21F33D),
        delivered = Color(0xFF21F33D)
    ),
    orderStatusContentColors = OrderStatusContentColors(
        processing = Color(0xFFE3F2FD),
        shipped = Color(0xFFFFF3E0),
        departedSortingFacility = Color(0xFFFFF3E0),
        outForDelivery = Color(0xFFFFF3E0),
        delivered = Color(0xFFE8F5E9)
    ),
    buttonBackgroundPositive = Color(0xFF21F33D),
    buttonBackgroundNegative = Color(0xFFD50000),
    buttonContentWhite = Color(0xFFF1F1F1),
    genericRed = Color(0xFFD50000),
    friendsDateColor = Color(0xFF2196F3),
    white = Color(0xFFFFFFFF),
    black = Color(0xFF000000),
    greenCheckmark = Color(0xFF21F33D),
    transparent = Color(0x00000000),
    info = Color(0xFF2196F3),
    cardBrandBackgroundColor = Color(0xFF1e40af),
    linkTextColor = Color(0xFF1e40af),
    genericOrangeColor = Color(0xFFD56E00),
    snackbarBackgroundColors = SnackbarBackgroundColors(
        info = Color(0xFF2196F3),
        warning = Color(0xFFFF9800),
        error = Color(0xFFD50000),
        success = Color(0xFF21F33D)
    ),
    listingStatusColors = ListingStatusColors(
        soldBackgroundColor = Color(0xA6B71C1C),
        soldOutlineColor = Color(0xFFB71C1C)
    ),
    infoStatusContainerColors = StatusContainerColors(
        backgroundColor = Color(0x1A3B82F6),
        outlineColor = Color(0x4D3B82F6),
        contentColor = Color(0xFF3B82F6)
    ),
    warningStatusContainerColors = StatusContainerColors(
        backgroundColor = Color(0x1AFF9800),
        outlineColor = Color(0x4DFF9800),
        contentColor = Color(0xFFFF9800)
    ),
    errorStatusContainerColors = StatusContainerColors(
        backgroundColor = Color(0x1AEF4444),
        outlineColor = Color(0x4DEF4444),
        contentColor = Color(0xFFEF4444)
    ),
    successStatusContainerColors = StatusContainerColors(
        backgroundColor = Color(0x1A21F33D),
        outlineColor = Color(0x4D21F33D),
        contentColor = Color(0xFF21F33D)
    ),
    creamWhite = Color(0xFFFFFDD0),
)

val LocalCustomColors = staticCompositionLocalOf { customLightColors() }

val MaterialTheme.customColors: CustomColors
    @Composable
    @ReadOnlyComposable
    get() = LocalCustomColors.current