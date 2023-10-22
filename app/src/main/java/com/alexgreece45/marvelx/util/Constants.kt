package com.alexgreece45.marvelx.util

import androidx.compose.ui.unit.dp

val DEFAULT_SCREEN_PADDING = 12.dp
val IMAGE_BORDER_WIDTH = 2.dp
const val MAX_CHARACTER_IMAGE_HEIGHT = 250

const val OTP_LENGTH = 6
const val APP_USER_AGENT = "mobile"

// Bottom Navigation Bar constants.
const val BOTTOM_NAVIGATION_BAR_MIN_ITEMS = 3
const val BOTTOM_NAVIGATION_BAR_MAX_ITEMS = 5
const val BOTTOM_NAVIGATION_BAR_ITEM_LABEL_MAX_LENGTH = 10

sealed class HeaderConstants(val name: String) {
    object UserAgent : HeaderConstants(name = "User-Agent")
    object Authorization : HeaderConstants(name = "Authorization")
}