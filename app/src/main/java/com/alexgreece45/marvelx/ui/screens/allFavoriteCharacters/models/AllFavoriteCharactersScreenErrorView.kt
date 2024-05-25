package com.alexgreece45.marvelx.ui.screens.allFavoriteCharacters.models

data class AllFavoriteCharactersScreenErrorView(
    val errorMessage: String,
    val onRetryClick: () -> Unit
)