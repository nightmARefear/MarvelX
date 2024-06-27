package com.alexgreece45.marvelx.domain.model_api.user.block

import com.alexgreece45.marvelx.data.model.RepositoryUserBlock

data class UserBlockDomain(
    val userBlockResult: UserBlockResult
)

internal fun RepositoryUserBlock.toUserBlockDomain(): UserBlockDomain =
    UserBlockDomain(
        userBlockResult = UserBlockResult.fromName(this.userBlockResult)
    )
