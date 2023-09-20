package com.alexgreece45.marvelx.domain.interactors

import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.data.api.ApiClient
import com.alexgreece45.marvelx.data.models.user.UserErrorResponse
import com.alexgreece45.marvelx.data.models.user.UserSignUpRequest
import com.alexgreece45.marvelx.data.models.user.UserSignUpResponse
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.extensions.safeAsync
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import javax.inject.Inject

interface UserInteractor {
    suspend fun postSignUp(userSignUpRequest: UserSignUpRequest): Flow<UserPartialStatePostSignUp>
}

class UserInteractorImpl @Inject constructor(
    private val api: ApiClient,
    private val resourceProvider: ResourceProvider,
    private val gson: Gson
) : UserInteractor {
    override suspend fun postSignUp(userSignUpRequest: UserSignUpRequest): Flow<UserPartialStatePostSignUp> =
        flow {
            val response = api.postSignUp(userSignUpRequest)

            if (response.isSuccessful) {
                response.body()?.let {
                    emit(
                        UserPartialStatePostSignUp.Success(
                            it
                        )
                    )
                } ?: run {
                    emit(
                        UserPartialStatePostSignUp.Error(
                            errorMessage = "An internal server error occurred!"
                        )
                    )
                }
            }

            // error
            val deserializedError =
                gson.fromJson(response.errorBody().toString(), UserErrorResponse::class.java)

            emit(
                UserPartialStatePostSignUp.Error(
                    errorMessage = deserializedError.errorMessage
                )
            )
        }.safeAsync {
            when (it) {
                is IOException -> {
                    UserPartialStatePostSignUp.NoInternetConnection

                }

                else -> {
                    UserPartialStatePostSignUp.Error(
                        errorMessage = resourceProvider.getString(R.string.unknown_error)
                    )
                }
            }
        }
}

sealed class UserPartialStatePostSignUp {
    data class Success(val userSignUpResponse: UserSignUpResponse) : UserPartialStatePostSignUp()

    data class Error(val errorMessage: String) : UserPartialStatePostSignUp()

    object NoInternetConnection : UserPartialStatePostSignUp()
}