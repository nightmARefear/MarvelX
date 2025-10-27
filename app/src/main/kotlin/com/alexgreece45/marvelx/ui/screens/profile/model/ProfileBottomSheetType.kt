package com.alexgreece45.marvelx.ui.screens.profile.model

/**
 * The bottom sheet types of the `Profile` screen.
 */
sealed interface ProfileBottomSheetType {
    data object ProfileImage : ProfileBottomSheetType
}
