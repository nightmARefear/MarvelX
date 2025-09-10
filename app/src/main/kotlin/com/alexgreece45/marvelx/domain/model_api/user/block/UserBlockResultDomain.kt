package com.alexgreece45.marvelx.domain.model_api.user.block

import com.alexgreece45.marvelx.data.model.RepositoryUserBlockResult

data class UserBlockResultDomain(
    val userBlockResultType: UserBlockResultType
)

internal fun RepositoryUserBlockResult.toUserBlockResultDomain(): UserBlockResultDomain =
    UserBlockResultDomain(
        userBlockResultType = UserBlockResultType.fromName(this.userBlockResult)
    )
