package com.alexgreece45.marvelx.data.models.user

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserSignUpResponse(
    val verificationCode : String
) : Parcelable
