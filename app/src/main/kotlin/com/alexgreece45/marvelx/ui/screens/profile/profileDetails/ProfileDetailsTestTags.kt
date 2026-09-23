package com.alexgreece45.marvelx.ui.screens.profile.profileDetails

internal object ProfileDetailsTestTags {
    // ProfileDetailsContract.Event.OnBackClicked
    const val BACK_BUTTON = "profile_details_back_button"

    // ProfileDetailsContract.Event.MoreActions.OnClicked (toolbar action)
    const val MORE_ACTIONS_BUTTON = "profile_details_more_actions_button"

    // ProfileDetailsContract.Event.OnEditProfileDetailsIconClicked (toolbar action, own profile only)
    const val EDIT_PROFILE_BUTTON = "profile_details_edit_profile_button"

    // ProfileDetailsContract.Event.ProfileBanner.OnProfileImageClicked
    const val PROFILE_IMAGE = "profile_details_profile_image"

    // ProfileDetailsContract.Event.ProfileBanner.OnFriendsListClicked
    const val FRIENDS_TILE = "profile_details_friends_tile"

    // ProfileDetailsContract.Event.ProfileBanner.OnListingsListClicked
    const val LISTINGS_TILE = "profile_details_listings_tile"

    // ProfileDetailsContract.Event.ProfileBanner.OnFavoritesListClicked
    const val FAVORITES_TILE = "profile_details_favorites_tile"

    // ProfileDetailsContract.Event.ProfileBanner.OnGenderLongClick (long click; visible gender only)
    const val GENDER_FIELD = "profile_details_gender_field"

    // ProfileDetailsContract.Event.ProfileBanner.OnEmailLongClick (long click; visible email only)
    const val EMAIL_FIELD = "profile_details_email_field"

    // ProfileDetailsContract.Event.ProfileBanner.OnMessageClicked (other users only)
    const val MESSAGE_CHIP = "profile_details_message_chip"

    // ProfileDetailsContract.Event.ProfileBanner.OnRelationshipStatusChipClicked; the same tag is
    // used for the `Add friend`, `Cancel request`, `Incoming request` and `Friends` states
    const val RELATIONSHIP_STATUS_CHIP = "profile_details_relationship_status_chip"

    // ProfileDetailsContract.Event.MoreActions.OnBlockClicked
    const val BLOCK_BOTTOM_SHEET_ITEM = "profile_details_block_bottom_sheet_item"

    // ProfileDetailsContract.Event.MoreActions.OnShareProfileClicked
    const val SHARE_PROFILE_BOTTOM_SHEET_ITEM = "profile_details_share_profile_bottom_sheet_item"

    // ProfileDetailsContract.Event.MoreActions.OnShowQrCodeClicked
    const val SHOW_QR_CODE_BOTTOM_SHEET_ITEM = "profile_details_show_qr_code_bottom_sheet_item"

    // The full screen error and alert dialog use the shared content error / alert dialog tags.
}
