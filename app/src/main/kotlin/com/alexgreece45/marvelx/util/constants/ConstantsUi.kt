package com.alexgreece45.marvelx.util.constants

import androidx.compose.ui.unit.dp

/**
 * UI - related constants.
 */
object ConstantsUi {
    object LoginScreen {
        const val STICKY_BOTTOM_BOTTOM_PADDING = 20
    }

    object FaqScreen {
        const val FAQ_SECTION_HEADER_ICON_TWEEN_MILLIS = 400
    }

    object ProfileScreen {
        val PROFILE_IMAGE_ELEVATION = 10.5.dp
    }

    object ProfileDetailsScreen {
        val PROFILE_BANNER_PADDING = 12.dp

        /**
         * The size of the QR code image.
         */
        const val QR_CODE_SIZE = 750

        val LOTTIE_ANIMATION_SIZE = 420.dp
    }

    object ProfileDetailsStatsScreen {
        val DATA_TILE_PADDING = 12.dp
    }

    object QrCodeScreen {
        val LOTTIE_ANIMATION_SIZE = 200.dp
    }

    /**
     * The standard border width of all images, expressed in density-independent pixels (dp).
     */
    val IMAGE_BORDER_WIDTH = 2.dp

    /**
     * The large size for profile images, expressed in density-independent pixels (dp).
     */
    val PROFILE_IMAGE_LARGE_SIZE = 150.dp

    /**
     * The small size for profile images, expressed in density-independent pixels (dp).
     */
    val PROFILE_IMAGE_SMALL_SIZE = PROFILE_IMAGE_LARGE_SIZE / 2.5f

    /**
     * The size for generic images in tiles, express in density-independent pixels (dp).
     */
    val GENERIC_IMAGE_TILE_SIZE = 60.dp
}