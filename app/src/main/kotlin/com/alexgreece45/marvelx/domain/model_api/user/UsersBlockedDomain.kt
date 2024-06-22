package com.alexgreece45.marvelx.domain.model_api.user

import com.alexgreece45.marvelx.data.model.RepositoryBasicProfileDetails
import com.alexgreece45.marvelx.data.model.RepositoryUsersBlocked
import com.alexgreece45.marvelx.domain.model_api.user.profileDetails.BasicProfileDetailsDomain
import com.alexgreece45.marvelx.network_logic.models_api.user.Gender

data class UsersBlockedDomain(
    val blockedUsers: List<BasicProfileDetailsDomain>
)

/**
 * Converts this instance to [RepositoryUsersBlocked].
 */
internal fun RepositoryUsersBlocked.toUsersBlockedDomain(): UsersBlockedDomain =
    UsersBlockedDomain(
        blockedUsers = this.blockedUsers.map { repositoryBasicProfileDetails ->
            repositoryBasicProfileDetails.toBasicProfileDetailsDomain()
        }
    )

/**
 * Converts this instance to [BasicProfileDetailsDomain] by setting an arbitrary gender to [Gender.Other],
 * since this field alongside [BasicProfileDetailsDomain.email] are hidden.
 */
private fun RepositoryBasicProfileDetails.toBasicProfileDetailsDomain(): BasicProfileDetailsDomain =
    BasicProfileDetailsDomain(
        firstName = this.firstName,
        lastName = this.lastName,
        gender = Gender.Other, // The gender's value is never shown, so no conversion is needed. An arbitrary value is set instead.
        email = "", // Set to empty.
        profileImageUrl = this.profileImageUrl.orEmpty(),
        fullName = this.firstName + " " + this.lastName
    )
