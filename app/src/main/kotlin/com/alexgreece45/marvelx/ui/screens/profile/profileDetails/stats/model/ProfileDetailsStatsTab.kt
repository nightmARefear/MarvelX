package com.alexgreece45.marvelx.ui.screens.profile.profileDetails.stats.model

import androidx.annotation.StringRes
import com.alexgreece45.marvelx.R

/**
 * Defines the data of a single tab in the `Profile Details Stats` screen.
 */
sealed class ProfileDetailsStatsTab(@StringRes val title: Int, val tabIndex: Int) {
    data object Friends : ProfileDetailsStatsTab(title = R.string.profile_details_stats_screen_label_tab_friends, tabIndex = 0)
    data object Listings : ProfileDetailsStatsTab(title = R.string.profile_details_stats_screen_label_tab_listings, tabIndex = 1)
    data object Favorites : ProfileDetailsStatsTab(title = R.string.profile_details_stats_screen_label_tab_favorites, tabIndex = 2)

    companion object {
        /**
         * Returns the first tab to be shown. Useful when initializing the tab layout.
         */
        fun getFirstTab(): ProfileDetailsStatsTab = Friends

        /**
         * Returns all the tabs.
         */
        fun getTabs(): List<ProfileDetailsStatsTab> =
            ProfileDetailsStatsTab::class.sealedSubclasses
                .mapNotNull { it.objectInstance }
                .map { it }
    }
}
