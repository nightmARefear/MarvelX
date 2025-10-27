package com.alexgreece45.marvelx.data.repository.helper

import com.alexgreece45.marvelx.network_logic.models_api.user.ResponseUserError
import com.google.gson.Gson
import retrofit2.Response

fun <T> parseUserError(response: Response<T>): ResponseUserError =
    Gson().fromJson(
        response.errorBody()?.string(),
        ResponseUserError::class.java
    )