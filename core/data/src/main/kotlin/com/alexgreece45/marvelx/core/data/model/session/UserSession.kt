package com.alexgreece45.marvelx.core.data.model.session

import com.alexgreece45.marvelx.model.model.AuthenticationTokens
import com.alexgreece45.marvelx.model.model.user.UserId

data class UserSession(
    val userId: UserId,
    val authenticationTokens: AuthenticationTokens
)