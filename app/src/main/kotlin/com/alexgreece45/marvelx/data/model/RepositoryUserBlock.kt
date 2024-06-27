package com.alexgreece45.marvelx.data.model

import com.alexgreece45.marvelx.network_logic.models_api.user.block.ResponseUserBlock

data class RepositoryUserBlock(
    val userBlockResult: String
)

internal fun ResponseUserBlock.toRepositoryUserBlock(): RepositoryUserBlock =
    RepositoryUserBlock(
        userBlockResult = this.userBlockResult
    )
