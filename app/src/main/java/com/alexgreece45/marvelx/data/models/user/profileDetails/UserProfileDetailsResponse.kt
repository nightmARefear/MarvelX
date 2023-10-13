package com.alexgreece45.marvelx.data.models.user.profileDetails

import com.alexgreece45.marvelx.data.domains.user.profileDetails.UserProfileDetailsDomain
import com.alexgreece45.marvelx.data.models.user.Gender
import com.alexgreece45.marvelx.util.extensions.or
import com.google.gson.annotations.SerializedName

data class UserProfileDetailsResponse(
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("email") val email: String,
)

fun UserProfileDetailsResponse.toUserProfileDetailsDomain(): UserProfileDetailsDomain =
    UserProfileDetailsDomain(
        firstName = this.firstName,
        lastName = this.lastName,
        gender = Gender.of(this.gender) ?: Gender.Other,
        email = this.email
    )
