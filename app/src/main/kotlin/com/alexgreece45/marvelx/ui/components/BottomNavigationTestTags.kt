package com.alexgreece45.marvelx.ui.components

/**
 * Tags for the dashboard's bottom navigation items. The bar is shared by every dashboard tab, so the
 * tags are fixed per destination rather than per screen.
 */
internal object BottomNavigationTestTags {
    // Navigates to the Marketplace tab
    const val MARKETPLACE_ITEM = "bottom_navigation_marketplace_item"

    // Navigates to the Inbox tab
    const val INBOX_ITEM = "bottom_navigation_inbox_item"

    // Navigates to the Favorites tab
    const val FAVORITES_ITEM = "bottom_navigation_favorites_item"

    // Navigates to the Cart tab
    const val CART_ITEM = "bottom_navigation_cart_item"

    // Navigates to the Profile tab
    const val PROFILE_ITEM = "bottom_navigation_profile_item"
}
