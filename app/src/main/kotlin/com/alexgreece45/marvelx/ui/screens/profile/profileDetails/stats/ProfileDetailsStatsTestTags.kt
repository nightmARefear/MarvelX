package com.alexgreece45.marvelx.ui.screens.profile.profileDetails.stats

internal object ProfileDetailsStatsTestTags {
    // ProfileDetailsStatsContract.Event.OnBackClicked
    const val BACK_BUTTON = "profile_details_stats_back_button"

    // ProfileDetailsStatsContract.Event.OnErrorCloseButtonClicked and the error primary button use
    // the shared content error tags (`content_error_close_button`, `content_error_primary_button`).

    /**
     * ProfileDetailsStatsContract.Event.OnItemClicked; one tag per friend / listing / favorite tile,
     * keyed by the item id (e.g. `profile_details_stats_item_42_tile`).
     */
    fun itemTile(itemId: Any) = "profile_details_stats_item_${itemId}_tile"
}
