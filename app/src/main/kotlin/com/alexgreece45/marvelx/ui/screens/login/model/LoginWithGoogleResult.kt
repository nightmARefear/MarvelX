package com.alexgreece45.marvelx.ui.screens.login.model

import android.content.IntentSender

sealed interface LoginWithGoogleResult {
    data class Success(val intentSender: IntentSender) : LoginWithGoogleResult
    data class Failed(val errorMessage: String) : LoginWithGoogleResult
}

