package com.alexgreece45.marvelx.ui.screens.allCharacters

internal object AllCharactersTestTags {
    // AllCharactersEvent.OnFabClick (opens the shared sorting bottom sheet, whose items and
    // `Close` button use the shared `sorting_*` tags)
    const val SORT_FAB = "all_characters_sort_fab"

    // Error `onRetryClick`
    const val ERROR_RETRY_BUTTON = "all_characters_error_retry_button"

    // AllCharactersEvent.SearchBar.OnKeyboardType
    const val SEARCH_TEXT_FIELD = "all_characters_search_text_field"

    // AllCharactersEvent.SearchBar.OnCloseClick
    const val SEARCH_CLEAR_BUTTON = "all_characters_search_clear_button"

    /**
     * AllCharactersEvent.OnCharacterClick; one tag per character, keyed by the character id
     * (e.g. `all_characters_character_1009610_card`).
     */
    fun characterCard(characterId: Any) = "all_characters_character_${characterId}_card"

    /**
     * AllCharactersEvent.OnHeartClick; one tag per character.
     */
    fun characterFavoriteButton(characterId: Any) =
        "all_characters_character_${characterId}_favorite_button"
}
