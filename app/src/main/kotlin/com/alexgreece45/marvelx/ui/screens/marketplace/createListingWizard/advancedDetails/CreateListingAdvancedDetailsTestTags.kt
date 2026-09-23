package com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.advancedDetails

/**
 * Tags for the create-listing advanced details step. Category-specific fields are only composed for
 * their category (apparel, drinkware, collectible, art, accessory, home, comic, card); the condition
 * dropdown and the price field are always present. Dropdown options are keyed by `EnumGeneric.code`.
 */
internal object CreateListingAdvancedDetailsTestTags {
    // ConditionDropDownEvents.OnClicked / OnDismissed
    const val CONDITION_DROPDOWN = "create_listing_advanced_details_condition_dropdown"

    // Event.OnPriceChanged
    const val PRICE_TEXT_FIELD = "create_listing_advanced_details_price_text_field"

    // ListingDetailsEvents.Apparel.OnSizeDropDownClicked / OnSizeDropDownOnDismissed
    const val APPAREL_SIZE_DROPDOWN = "create_listing_advanced_details_apparel_size_dropdown"

    // ListingDetailsEvents.Apparel.OnColorChanged
    const val APPAREL_COLOR_TEXT_FIELD = "create_listing_advanced_details_apparel_color_text_field"

    // ListingDetailsEvents.Drinkware.OnMaterialDropDownClicked / OnMaterialDropDownOnDismissed
    const val DRINKWARE_MATERIAL_DROPDOWN =
        "create_listing_advanced_details_drinkware_material_dropdown"

    // ListingDetailsEvents.Drinkware.OnCapacityMlChanged
    const val DRINKWARE_CAPACITY_TEXT_FIELD =
        "create_listing_advanced_details_drinkware_capacity_text_field"

    // ListingDetailsEvents.Drinkware.OnDishwasherSafeCheckboxChanged
    const val DRINKWARE_DISHWASHER_SAFE_CHECKBOX =
        "create_listing_advanced_details_drinkware_dishwasher_safe_checkbox"

    // ListingDetailsEvents.Collectible.OnScaleDropDownClicked / OnScaleDropDownOnDismissed
    const val COLLECTIBLE_SCALE_DROPDOWN =
        "create_listing_advanced_details_collectible_scale_dropdown"

    // ListingDetailsEvents.Collectible.OnBoxConditionDropDownClicked / OnBoxConditionDropDownOnDismissed
    const val COLLECTIBLE_BOX_CONDITION_DROPDOWN =
        "create_listing_advanced_details_collectible_box_condition_dropdown"

    // ListingDetailsEvents.Art.OnDimensionsWidthChanged
    const val ART_WIDTH_TEXT_FIELD = "create_listing_advanced_details_art_width_text_field"

    // ListingDetailsEvents.Art.OnDimensionsHeightChanged
    const val ART_HEIGHT_TEXT_FIELD = "create_listing_advanced_details_art_height_text_field"

    // ListingDetailsEvents.Art.OnFramedCheckboxChanged
    const val ART_FRAMED_CHECKBOX = "create_listing_advanced_details_art_framed_checkbox"

    // ListingDetailsEvents.Accessory.OnMaterialDropDownClicked / OnMaterialDropDownOnDismissed
    const val ACCESSORY_MATERIAL_DROPDOWN =
        "create_listing_advanced_details_accessory_material_dropdown"

    // ListingDetailsEvents.Accessory.OnColorChanged
    const val ACCESSORY_COLOR_TEXT_FIELD =
        "create_listing_advanced_details_accessory_color_text_field"

    // ListingDetailsEvents.Home.OnDimensionsWidthChanged
    const val HOME_WIDTH_TEXT_FIELD = "create_listing_advanced_details_home_width_text_field"

    // ListingDetailsEvents.Home.OnDimensionsHeightChanged
    const val HOME_HEIGHT_TEXT_FIELD = "create_listing_advanced_details_home_height_text_field"

    // ListingDetailsEvents.Comic.OnGradeDropDownClicked / OnGradeDropDownOnDismissed
    const val COMIC_GRADE_DROPDOWN = "create_listing_advanced_details_comic_grade_dropdown"

    // ListingDetailsEvents.Comic.OnPublisherChanged
    const val COMIC_PUBLISHER_TEXT_FIELD =
        "create_listing_advanced_details_comic_publisher_text_field"

    // ListingDetailsEvents.Comic.OnYearChanged
    const val COMIC_YEAR_TEXT_FIELD = "create_listing_advanced_details_comic_year_text_field"

    // ListingDetailsEvents.CardDetails.OnGradeDropDownClicked / OnGradeDropDownOnDismissed
    const val CARD_GRADE_DROPDOWN = "create_listing_advanced_details_card_grade_dropdown"

    // ListingDetailsEvents.CardDetails.OnRarityDropDownClicked / OnRarityDropDownOnDismissed
    const val CARD_RARITY_DROPDOWN = "create_listing_advanced_details_card_rarity_dropdown"

    // ListingDetailsEvents.CardDetails.OnVariantDropDownClicked / OnVariantDropDownOnDismissed
    const val CARD_VARIANT_DROPDOWN = "create_listing_advanced_details_card_variant_dropdown"

    // ConditionDropDownEvents.OnItemSelected
    fun conditionOption(code: String) = "create_listing_advanced_details_condition_${code}_option"

    // ListingDetailsEvents.Apparel.OnSizeDropDownItemSelected
    fun apparelSizeOption(code: String) = "create_listing_advanced_details_apparel_size_${code}_option"

    // ListingDetailsEvents.Drinkware.OnMaterialDropDownItemSelected
    fun drinkwareMaterialOption(code: String) =
        "create_listing_advanced_details_drinkware_material_${code}_option"

    // ListingDetailsEvents.Collectible.OnScaleDropDownItemSelected
    fun collectibleScaleOption(code: String) =
        "create_listing_advanced_details_collectible_scale_${code}_option"

    // ListingDetailsEvents.Collectible.OnBoxConditionDropDownItemSelected
    fun collectibleBoxConditionOption(code: String) =
        "create_listing_advanced_details_collectible_box_condition_${code}_option"

    // ListingDetailsEvents.Accessory.OnMaterialDropDownItemSelected
    fun accessoryMaterialOption(code: String) =
        "create_listing_advanced_details_accessory_material_${code}_option"

    // ListingDetailsEvents.Comic.OnGradeDropDownItemSelected
    fun comicGradeOption(code: String) = "create_listing_advanced_details_comic_grade_${code}_option"

    // ListingDetailsEvents.CardDetails.OnGradeDropDownItemSelected
    fun cardGradeOption(code: String) = "create_listing_advanced_details_card_grade_${code}_option"

    // ListingDetailsEvents.CardDetails.OnRarityDropDownItemSelected
    fun cardRarityOption(code: String) = "create_listing_advanced_details_card_rarity_${code}_option"

    // ListingDetailsEvents.CardDetails.OnVariantDropDownItemSelected
    fun cardVariantOption(code: String) = "create_listing_advanced_details_card_variant_${code}_option"
}
