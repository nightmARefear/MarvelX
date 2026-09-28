package com.alexgreece45.marvelx.ui.screens.profile

internal object ProfileTestTags {
    // ProfileContract.Event.OnSettingsIconButtonClicked (toolbar action)
    const val SETTINGS_BUTTON = "profile_settings_button"

    // ProfileContract.Event.ProfileImage.OnClicked (opens the profile image bottom sheet)
    const val PROFILE_IMAGE = "profile_profile_image"

    // ProfileContract.Event.OnSeeYourProfileClicked
    const val SEE_YOUR_PROFILE_BUTTON = "profile_see_your_profile_button"

    // Section: Scan QR code; navigates to the QR code screen
    const val SCAN_QR_CODE_SECTION = "profile_scan_qr_code_section"

    // Section: Friends; navigates to the Friends screen
    const val FRIENDS_SECTION = "profile_friends_section"

    // Section: Orders; navigates to the Orders screen
    const val ORDERS_SECTION = "profile_orders_section"

    // Section: Transactions; navigates to the Transactions screen
    const val TRANSACTIONS_SECTION = "profile_transactions_section"

    // Section: My listings; navigates to the My Listings screen
    const val MY_LISTINGS_SECTION = "profile_my_listings_section"

    // ProfileContract.Event.ProfileImage.BottomSheet.OnViewImageClicked
    const val VIEW_PROFILE_IMAGE_BOTTOM_SHEET_ITEM = "profile_view_profile_image_bottom_sheet_item"

    // ProfileContract.Event.ProfileImage.BottomSheet.OnUploadImageClicked
    const val UPLOAD_PROFILE_IMAGE_BOTTOM_SHEET_ITEM =
        "profile_upload_profile_image_bottom_sheet_item"

    // ProfileContract.Event.ProfileImage.CroppedImage.OnCloseClicked (toolbar `Close` button)
    const val CROPPED_IMAGE_CLOSE_BUTTON = "profile_cropped_image_close_button"

    // ProfileContract.Event.ProfileImage.CroppedImage.OnSaveClicked
    const val CROPPED_IMAGE_SAVE_BUTTON = "profile_cropped_image_save_button"

    // The error screen and alert dialog use the shared content error / alert dialog tags.
}
