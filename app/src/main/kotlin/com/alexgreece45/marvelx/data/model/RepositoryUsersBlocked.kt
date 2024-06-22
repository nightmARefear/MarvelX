package com.alexgreece45.marvelx.data.model

import com.alexgreece45.marvelx.network_logic.models_api.user.ResponseUsersBlocked

data class RepositoryUsersBlocked(
    val blockedUsers: List<RepositoryBasicProfileDetails>
)

/**
 * Converts this instance to [RepositoryUsersBlocked].
 */
internal fun ResponseUsersBlocked.toRepositoryUsersBlocked(): RepositoryUsersBlocked =
    RepositoryUsersBlocked(
        blockedUsers = this.blockedUsers.map { it.toRepositoryBasicProfileDetails() }
    )
