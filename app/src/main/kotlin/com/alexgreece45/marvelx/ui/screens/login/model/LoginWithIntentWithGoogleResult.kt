package com.alexgreece45.marvelx.ui.screens.login.model

sealed interface LoginWithIntentWithGoogleResult {
    data class Success(val googleUserDomain: GoogleUserDomain?) : LoginWithIntentWithGoogleResult
    data class Failed(val errorMessage: String) : LoginWithIntentWithGoogleResult
    data object Cancelled : LoginWithIntentWithGoogleResult
    data object UnknownError : LoginWithIntentWithGoogleResult
}