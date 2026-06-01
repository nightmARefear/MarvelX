package com.alexgreece45.marvelx.data.model

import com.alexgreece45.marvelx.core.network.model.api.user.block.ResponseUserBlockResult

data class RepositoryUserBlockResult(
    val userBlockResult: String
)

internal fun com.alexgreece45.marvelx.core.network.model.api.user.block.ResponseUserBlockResult.toRepositoryUserBlock(): RepositoryUserBlockResult =
    RepositoryUserBlockResult(
        userBlockResult = this.userBlockResult
    )
