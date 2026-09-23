package com.alexgreece45.marvelx.ui.screens.character

internal object CharacterTestTags {
    // CharacterEvent.OnBackClick
    const val BACK_BUTTON = "character_back_button"

    // CharacterEvent.OnFavoriteClick (toolbar action)
    const val FAVORITE_BUTTON = "character_favorite_button"

    // Error `onRetryClick`
    const val ERROR_RETRY_BUTTON = "character_error_retry_button"

    // CharacterEvent.OnArrowRightClick (`See all comics`)
    const val ALL_COMICS_BUTTON = "character_all_comics_button"

    /**
     * CharacterEvent.OnComicClick; one tag per comic, keyed by the comic id
     * (e.g. `character_comic_1234_item`).
     */
    fun comicItem(comicId: Any) = "character_comic_${comicId}_item"
}
