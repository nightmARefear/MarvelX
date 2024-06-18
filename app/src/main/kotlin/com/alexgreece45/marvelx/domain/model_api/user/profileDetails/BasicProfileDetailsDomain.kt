package com.alexgreece45.marvelx.domain.model_api.user.profileDetails

import com.alexgreece45.marvelx.network_logic.models_api.user.Gender
import com.alexgreece45.marvelx.network_logic.models_api.user.profileDetails.ResponseBasicProfileDetails

data class BasicProfileDetailsDomain(
    val firstName: String,
    val lastName: String,
    val gender: Gender,
    val email: String,
    val profileImageUrl: String,
    val fullName: String,
)

/**
 * Converts this instance to [BasicProfileDetailsDomain]. If [isGenderVisible] is `true` then the
 * actual gender is shown, otherwise [Gender.Other].
 */
fun ResponseBasicProfileDetails.toBasicProfileDetailsDomain(isGenderVisible: Boolean): BasicProfileDetailsDomain =
    BasicProfileDetailsDomain(
        firstName = this.firstName,
        lastName = this.lastName,
        gender = if (isGenderVisible) Gender.of(this.gender) else Gender.Other,
        email = this.email,
        profileImageUrl = this.profileImageUrl.orEmpty(),
        fullName = "${this.firstName} ${this.lastName}"
    )