package com.alexgreece45.marvelx.ui.components

/**
 * Shared tags for the sorting bottom sheet ([SortingModalBottomSheetContent]). The sheet is used by
 * several screens, so the tags are fixed per role rather than per screen.
 */
internal object SortingTestTags {
    // `onCloseClick`
    const val CLOSE_BUTTON = "sorting_close_button"

    /**
     * `onItemClick`; one tag per [SortingType] (e.g. `sorting_text_ascending_item`).
     */
    fun item(sortingType: SortingType): String = when (sortingType) {
        SortingType.Random -> "sorting_random_item"
        SortingType.TextAscending -> "sorting_text_ascending_item"
        SortingType.TextDescending -> "sorting_text_descending_item"
    }
}
