package com.alexgreece45.marvelx.ui.screens.marketplace.model

import androidx.annotation.StringRes
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.core.data.model.marketplace.ListingDateRangeOption

internal sealed class ListingDateRangeOptionUi(@StringRes val labelResId: Int) {

    data object All : ListingDateRangeOptionUi(R.string.listing_date_range_option_all_label)
    data object Last24Hours :
        ListingDateRangeOptionUi(R.string.listing_date_range_option_last_24_hours_label)

    data object Last7Days :
        ListingDateRangeOptionUi(R.string.listing_date_range_option_last_7_days_label)

    data object Last30Days :
        ListingDateRangeOptionUi(R.string.listing_date_range_option_last_30_days_label)

    data object Custom : ListingDateRangeOptionUi(R.string.listing_date_range_option_custom_label)

    companion object {
        val entries get() = listOf(All, Last24Hours, Last7Days, Last30Days, Custom)
    }
}

internal fun ListingDateRangeOption.toUi(): ListingDateRangeOptionUi =
    when (this) {
        ListingDateRangeOption.ALL -> ListingDateRangeOptionUi.All
        ListingDateRangeOption.LAST_24_HOURS -> ListingDateRangeOptionUi.Last24Hours
        ListingDateRangeOption.LAST_7_DAYS -> ListingDateRangeOptionUi.Last7Days
        ListingDateRangeOption.LAST_30_DAYS -> ListingDateRangeOptionUi.Last30Days
        ListingDateRangeOption.CUSTOM -> ListingDateRangeOptionUi.Custom
    }

internal fun ListingDateRangeOptionUi.toData(): ListingDateRangeOption =
    when (this) {
        ListingDateRangeOptionUi.All -> ListingDateRangeOption.ALL
        ListingDateRangeOptionUi.Last24Hours -> ListingDateRangeOption.LAST_24_HOURS
        ListingDateRangeOptionUi.Last7Days -> ListingDateRangeOption.LAST_7_DAYS
        ListingDateRangeOptionUi.Last30Days -> ListingDateRangeOption.LAST_30_DAYS
        ListingDateRangeOptionUi.Custom -> ListingDateRangeOption.CUSTOM
    }