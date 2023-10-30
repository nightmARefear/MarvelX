package com.alexgreece45.marvelx.util

import androidx.compose.ui.unit.dp

const val OTP_LENGTH = 6
const val APP_USER_AGENT = "mobile"

// OAuth constants.
const val clientId = "mobile"
const val redirectScheme = "marvelxbackend"
const val redirectHost = "oauth"
const val redirectUri = "$redirectScheme//$redirectHost"

val DEFAULT_SCREEN_PADDING = 12.dp
val IMAGE_BORDER_WIDTH = 2.dp
const val MAX_CHARACTER_IMAGE_HEIGHT = 250

// Bottom Navigation Bar constants.
const val BOTTOM_NAVIGATION_BAR_MIN_ITEMS = 3
const val BOTTOM_NAVIGATION_BAR_MAX_ITEMS = 5
const val BOTTOM_NAVIGATION_BAR_ITEM_LABEL_MAX_LENGTH = 10

// Throttle clickable constants.
const val THROTTLE_CLICKABLE_DEFAULT_DELAY: Long = 1000

sealed class HeaderConstants(val name: String) {
    object UserAgent : HeaderConstants(name = "User-Agent")
    object Authorization : HeaderConstants(name = "Authorization")
}