package com.alexgreece45.marvelx.util

import androidx.compose.ui.unit.dp

const val OTP_LENGTH = 6
const val APP_USER_AGENT = "mobile"

// OAuth constants.
const val TOKEN_CLIENT_ID = "mobile"
const val TOKEN_REDIRECT_SCHEME = "marvelxbackend"
const val TOKEN_REDIRECT_HOST = "oauth"
const val TOKEN_REDIRECT_URI = "$TOKEN_REDIRECT_SCHEME//$TOKEN_REDIRECT_HOST"

val DEFAULT_SCREEN_PADDING = 12.dp
val IMAGE_BORDER_WIDTH = 2.dp
const val MAX_CHARACTER_IMAGE_HEIGHT = 250

// Bottom Navigation Bar constants.
const val BOTTOM_NAVIGATION_BAR_MIN_ITEMS = 3
const val BOTTOM_NAVIGATION_BAR_MAX_ITEMS = 5
const val BOTTOM_NAVIGATION_BAR_ITEM_LABEL_MAX_LENGTH = 10

// Throttle clickable constants.
const val THROTTLE_CLICKABLE_DEFAULT_DELAY: Long = 1000

// Work manager constants.
const val REFRESH_TOKEN_WORKER_NAME = "refresh-token-work"

sealed class HeaderConstants(val name: String) {
    data object UserAgent : HeaderConstants(name = "User-Agent")
    data object Authorization : HeaderConstants(name = "Authorization")
}