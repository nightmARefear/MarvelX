package com.alexgreece45.marvelx.ui.screens.orders.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.data.model.cardBrand.CardBrandData

@Immutable
sealed class CardBrandUi(
    @StringRes val labelResId: Int,
    @StringRes val contentDescriptionResId: Int,
    @DrawableRes val iconResId: Int,
    val regex: Regex?
) {
    object MasterCard : CardBrandUi(
        labelResId = R.string.card_brand_label_mastercard,
        contentDescriptionResId = R.string.card_brand_content_description_mastercard,
        iconResId = R.drawable.ic_mastercard_logo_32,
        regex = Regex("^5[1-5][0-9]{5,}|222[1-9][0-9]{3,}|22[3-9][0-9]{4,}|2[3-6][0-9]{5,}|27[01][0-9]{4,}|2720[0-9]{3,}\$")
    )

    object MaestroCard : CardBrandUi(
        labelResId = R.string.card_brand_label_maestro,
        contentDescriptionResId = R.string.card_brand_content_description_maestro,
        iconResId = R.drawable.ic_maestro_card_logo_32,
        regex = Regex("^(5018|5020|5038|5893|6304|6759|6761|6762|6763)[0-9]{8,15}\$")
    )

    object Visa : CardBrandUi(
        labelResId = R.string.card_brand_label_visa,
        contentDescriptionResId = R.string.card_brand_content_description_visa,
        iconResId = R.drawable.ic_visa_logo_32,
        regex = Regex("^4[0-9]{6,}\$")
    )

    object AmericanExpress : CardBrandUi(
        labelResId = R.string.card_brand_label_american_express,
        contentDescriptionResId = R.string.card_brand_content_description_american_express,
        iconResId = R.drawable.ic_american_express_card_logo_32,
        regex = Regex("^3[47][0-9]{5,}\$")
    )

    object DinersClub : CardBrandUi(
        labelResId = R.string.card_brand_label_dinersclub,
        contentDescriptionResId = R.string.card_brand_content_description_dinersclub,
        iconResId = R.drawable.ic_dinersclub_card_logo_32,
        regex = Regex("^3(?:0[0-5]|[68][0-9])[0-9]{4,}\$")
    )

    object Discover : CardBrandUi(
        labelResId = R.string.card_brand_label_discover,
        contentDescriptionResId = R.string.card_brand_content_description_discover,
        iconResId = R.drawable.ic_discover_card_logo_32,
        regex = Regex("^6(?:011|5[0-9]{2})[0-9]{3,}\$")
    )

    object Unknown :
        CardBrandUi(
            labelResId = R.string.card_brand_label_unknown,
            contentDescriptionResId = R.string.card_brand_content_description_unknown,
            iconResId = R.drawable.ic_card_brand_unknown,
            regex = null
        )

    companion object {
        private fun asList(): List<CardBrandUi> =
            listOf(MasterCard, MaestroCard, Visa, AmericanExpress, DinersClub, Discover, Unknown)

        fun getCardBrandUi(cardNumber: String): CardBrandUi =
            asList().firstOrNull { brand ->
                brand.regex?.matches(cardNumber) == true
            } ?: Unknown
    }
}

fun CardBrandData.toUi(): CardBrandUi =
    when (this) {
        CardBrandData.MASTERCARD -> CardBrandUi.MasterCard
        CardBrandData.MAESTRO_CARD -> CardBrandUi.MaestroCard
        CardBrandData.VISA -> CardBrandUi.Visa
        CardBrandData.AMERICAN_EXPRESS -> CardBrandUi.AmericanExpress
        CardBrandData.DINERS_CLUB -> CardBrandUi.DinersClub
        CardBrandData.DISCOVER -> CardBrandUi.Discover
        CardBrandData.UNKNOWN -> CardBrandUi.Unknown
    }

fun CardBrandUi.toData(): CardBrandData =
    when (this) {
        CardBrandUi.MasterCard -> CardBrandData.MASTERCARD
        CardBrandUi.MaestroCard -> CardBrandData.MAESTRO_CARD
        CardBrandUi.Visa -> CardBrandData.VISA
        CardBrandUi.AmericanExpress -> CardBrandData.AMERICAN_EXPRESS
        CardBrandUi.DinersClub -> CardBrandData.DINERS_CLUB
        CardBrandUi.Discover -> CardBrandData.DISCOVER
        CardBrandUi.Unknown -> CardBrandData.UNKNOWN
    }