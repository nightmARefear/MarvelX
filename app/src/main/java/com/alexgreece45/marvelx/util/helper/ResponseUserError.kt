package com.alexgreece45.marvelx.util.helper

import com.alexgreece45.marvelx.data.models_api.user.ResponseUserError
import com.google.gson.Gson
import retrofit2.Response

fun <T> parseError(response: Response<T>): ResponseUserError =
    Gson().fromJson(
        response.errorBody()?.string(),
        ResponseUserError::class.java
    )