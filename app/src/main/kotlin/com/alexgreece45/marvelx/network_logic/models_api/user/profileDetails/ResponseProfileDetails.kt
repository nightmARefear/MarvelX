package com.alexgreece45.marvelx.network_logic.models_api.user.profileDetails

import com.alexgreece45.marvelx.domain.models_api.user.profileDetails.ProfileDetailsDomain
import com.alexgreece45.marvelx.network_logic.models_api.user.Gender
import com.google.gson.annotations.SerializedName

data class ResponseProfileDetails(
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("email") val email: String,
)

fun ResponseProfileDetails.toProfileDetailsDomain(): ProfileDetailsDomain =
    ProfileDetailsDomain(
        firstName = this.firstName,
        lastName = this.lastName,
        gender = Gender.of(this.gender) ?: Gender.Other,
        email = this.email
    )
