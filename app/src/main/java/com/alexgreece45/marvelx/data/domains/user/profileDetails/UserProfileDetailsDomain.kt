package com.alexgreece45.marvelx.data.domains.user.profileDetails

import com.alexgreece45.marvelx.data.models.user.Gender

data class UserProfileDetailsDomain(
    val firstName: String,
    val lastName: String,
    val fullName: String = "$firstName $lastName",
    val gender: Gender,
    val email: String,
)
