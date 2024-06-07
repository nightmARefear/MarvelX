package com.alexgreece45.marvelx.ui.screens.allFavoriteCharacters.model

data class AllFavoriteCharactersScreenErrorView(
    val errorMessage: String,
    val onRetryClick: () -> Unit
)