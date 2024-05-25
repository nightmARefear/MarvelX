package com.alexgreece45.marvelx.ui.screens.allCharacters.models

data class AllCharactersScreenErrorView(
    val errorMessage: String,
    val onRetryClick: () -> Unit
)