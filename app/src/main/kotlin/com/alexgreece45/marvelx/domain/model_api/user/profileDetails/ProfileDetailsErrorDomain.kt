package com.alexgreece45.marvelx.domain.model_api.user.profileDetails

import com.alexgreece45.marvelx.core.network.model.api.user.profileDetails.ResponseProfileDetailsError

data class ProfileDetailsErrorDomain(
    val profileDetailsErrorType: ProfileDetailsErrorType
)

internal fun ResponseProfileDetailsError.toProfileDetailsErrorDomain(): ProfileDetailsErrorDomain =
    ProfileDetailsErrorDomain(
        profileDetailsErrorType = ProfileDetailsErrorType.fromKey(this.getProfileDetailsErrorType)
    )
