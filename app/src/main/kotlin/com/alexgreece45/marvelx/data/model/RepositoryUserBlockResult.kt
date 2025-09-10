package com.alexgreece45.marvelx.data.model

import com.alexgreece45.marvelx.network_logic.models_api.user.block.ResponseUserBlockResult

data class RepositoryUserBlockResult(
    val userBlockResult: String
)

internal fun ResponseUserBlockResult.toRepositoryUserBlock(): RepositoryUserBlockResult =
    RepositoryUserBlockResult(
        userBlockResult = this.userBlockResult
    )
