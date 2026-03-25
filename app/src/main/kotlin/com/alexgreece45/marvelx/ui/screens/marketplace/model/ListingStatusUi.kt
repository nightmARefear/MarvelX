package com.alexgreece45.marvelx.ui.screens.marketplace.model

import androidx.annotation.StringRes
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.ListingStatusDomain

enum class ListingStatusUi(@StringRes val labelResId: Int) {
    Draft(labelResId = R.string.listing_status_ui_label_draft),
    Active(labelResId = R.string.listing_status_ui_label_active),
    Reserved(labelResId = R.string.listing_status_ui_label_reserved),
    Sold(labelResId = R.string.listing_status_ui_label_sold);
}

fun ListingStatusDomain.toUi(): ListingStatusUi =
    when (this) {
        ListingStatusDomain.Draft -> ListingStatusUi.Draft
        ListingStatusDomain.Active -> ListingStatusUi.Active
        ListingStatusDomain.Reserved -> ListingStatusUi.Reserved
        ListingStatusDomain.Sold -> ListingStatusUi.Sold
    }
