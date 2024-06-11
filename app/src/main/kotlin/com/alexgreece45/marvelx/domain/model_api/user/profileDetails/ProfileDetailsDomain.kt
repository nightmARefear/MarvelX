package com.alexgreece45.marvelx.domain.model_api.user.profileDetails

import com.alexgreece45.marvelx.network_logic.models_api.user.Gender
import com.alexgreece45.marvelx.network_logic.models_api.user.profileDetails.ResponseProfileDetails

data class ProfileDetailsDomain(
    val firstName: String,
    val lastName: String,
    val fullName: String = "$firstName $lastName",
    val gender: Gender,
    val email: String,
)

fun ResponseProfileDetails.toProfileDetailsDomain(): ProfileDetailsDomain =
    ProfileDetailsDomain(
        firstName = this.firstName,
        lastName = this.lastName,
        gender = Gender.of(this.gender) ?: Gender.Other,
        email = this.email
    )

