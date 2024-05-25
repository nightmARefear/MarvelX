package com.alexgreece45.marvelx.domain.models_api.user.profileDetails

import com.alexgreece45.marvelx.data.models_api.user.Gender

data class ProfileDetailsDomain(
    val firstName: String,
    val lastName: String,
    val fullName: String = "$firstName $lastName",
    val gender: Gender,
    val email: String,
)
