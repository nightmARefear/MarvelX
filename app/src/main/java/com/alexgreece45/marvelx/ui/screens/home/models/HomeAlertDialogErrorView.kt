package com.alexgreece45.marvelx.ui.screens.home.models

data class HomeAlertDialogErrorView(
    val title: String,
    val errorMessage: String,
    val buttonLabel: String,
    val alertDialogOnClick: () -> Unit
)