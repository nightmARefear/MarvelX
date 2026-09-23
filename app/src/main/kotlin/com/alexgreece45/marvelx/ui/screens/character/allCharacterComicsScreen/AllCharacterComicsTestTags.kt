package com.alexgreece45.marvelx.ui.screens.character.allCharacterComicsScreen

internal object AllCharacterComicsTestTags {
    // AllCharacterComicsEvent.OnBackClick
    const val BACK_BUTTON = "all_character_comics_back_button"

    // Error `onRetryClick`
    const val ERROR_RETRY_BUTTON = "all_character_comics_error_retry_button"

    // AllCharacterComicsEvent.Init (retry after losing the internet connection)
    const val NO_INTERNET_RETRY_BUTTON = "all_character_comics_no_internet_retry_button"

    // AllCharacterComicsEvent.OnDropDownArrowClick (opens the shared sorting bottom sheet, whose
    // items and `Close` button use the shared `sorting_*` tags)
    const val SORT_DROPDOWN = "all_character_comics_sort_dropdown"

    /**
     * AllCharacterComicsEvent.OnComicClick; one tag per comic, keyed by the comic id
     * (e.g. `all_character_comics_comic_1234_item`).
     */
    fun comicItem(comicId: Any) = "all_character_comics_comic_${comicId}_item"
}
