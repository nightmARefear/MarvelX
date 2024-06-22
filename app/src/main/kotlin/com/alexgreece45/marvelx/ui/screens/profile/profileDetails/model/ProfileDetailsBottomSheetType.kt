package com.alexgreece45.marvelx.ui.screens.profile.profileDetails.model

import android.graphics.Bitmap

/**
 * Defines a single bottom sheet type of the `Profile Details` screen.
 */
sealed interface ProfileDetailsBottomSheetType {
    /**
     * The more actions bottom sheet.
     */
    data object MoreActions : ProfileDetailsBottomSheetType

    data class QrCode(val qrBitmap: Bitmap) : ProfileDetailsBottomSheetType
}