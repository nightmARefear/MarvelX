package com.alexgreece45.marvelx.core.network.constants

import com.alexgreece45.marvelx.core.common.extensions.plusDays
import com.alexgreece45.marvelx.core.common.extensions.plusMinutes
import com.alexgreece45.marvelx.core.common.util.DateUtil
import com.alexgreece45.marvelx.core.network.model.api.token.ResponseToken

internal object TokenConstants {
    val responseToken = ResponseToken(
        accessToken = "ACCESS_TOKEN",
        accessExpiresIn = DateUtil.getUTCInstant().plusMinutes(15L).epochSecond.toInt(),
        refreshToken = "REFRESH_TOKEN",
        refreshExpiresIn = DateUtil.getUTCInstant().plusDays(3L).epochSecond.toInt(),
        tokenType = "Bearer"
    )
}