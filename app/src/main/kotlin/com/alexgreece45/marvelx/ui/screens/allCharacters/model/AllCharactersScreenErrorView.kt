package com.alexgreece45.marvelx.ui.screens.allCharacters.model

data class AllCharactersScreenErrorView(
    val errorMessage: String,
    val onRetryClick: () -> Unit
)
