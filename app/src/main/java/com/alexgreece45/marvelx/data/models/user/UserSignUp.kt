package com.alexgreece45.marvelx.data.models.user

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserSignUp(
    val firstName: String,
    val lastName: String ,
    val gender: Gender,
    val email: String,
    val password: String,
) : Parcelable