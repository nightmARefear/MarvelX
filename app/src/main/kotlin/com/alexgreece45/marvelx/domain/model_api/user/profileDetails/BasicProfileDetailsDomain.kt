package com.alexgreece45.marvelx.domain.model_api.user.profileDetails

import com.alexgreece45.marvelx.network_logic.models_api.user.Gender
import com.alexgreece45.marvelx.network_logic.models_api.user.profileDetails.ResponseBasicProfileDetails
import com.alexgreece45.marvelx.domain.model_generic.ReloadableImageUrlData
import com.alexgreece45.marvelx.util.extensions.toFullName

data class BasicProfileDetailsDomain(
    val firstName: String,
    val lastName: String,
    val gender: Gender,
    val email: String,
    val profileImageUrlData: ReloadableImageUrlData,
) {

    val fullName: String get() = Pair(firstName, lastName).toFullName()
}

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
        profileImageUrlData = ReloadableImageUrlData(this.profileImageUrl.orEmpty())
    )
