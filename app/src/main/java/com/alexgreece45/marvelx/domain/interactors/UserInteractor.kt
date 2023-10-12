package com.alexgreece45.marvelx.domain.interactors

import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.data.api.ApiClient
import com.alexgreece45.marvelx.data.domains.user.UserLogInResponseDomain
import com.alexgreece45.marvelx.data.models.user.UserResponseError
import com.alexgreece45.marvelx.data.models.user.logIn.UserLogInRequest
import com.alexgreece45.marvelx.data.models.user.logIn.toDomain
import com.alexgreece45.marvelx.data.models.user.signUp.UserSignUpRequest
import com.alexgreece45.marvelx.data.models.user.signUp.UserSignUpResponse
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.extensions.safeAsync
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody
import okio.IOException
import javax.inject.Inject

interface UserInteractor {
    suspend fun postSignUp(userSignUpRequest: UserSignUpRequest): Flow<UserPartialStatePostSignUp>
    suspend fun postLogIn(userLogInRequest: UserLogInRequest): Flow<UserPartialStatePostLogIn>
}

class UserInteractorImpl @Inject constructor(
    private val api: ApiClient,
    private val resourceProvider: ResourceProvider,
    private val gson: Gson
) : UserInteractor {
    override suspend fun postSignUp(userSignUpRequest: UserSignUpRequest): Flow<UserPartialStatePostSignUp> =
        flow {
            api.postSignUp(userSignUpRequest).let { response ->
                if (response.isSuccessful) {
                    // Success.
                    response.body()?.let { responseBody ->
                        emit(
                            UserPartialStatePostSignUp.Success(
                                userSignUpResponse = responseBody
                            )
                        )
                    } ?: run {
                        // Null body, thus server responded erroneously.
                        emit(
                            UserPartialStatePostSignUp.Error(
                                errorMessage = resourceProvider.getString(R.string.internal_server_error)
                            )
                        )
                    }
                } else {
                    // Error.
                    val deserializedError =
                        gson.fromJson(
                            response.errorBody().toString(),
                            UserResponseError::class.java
                        )

                    emit(
                        UserPartialStatePostSignUp.Error(
                            errorMessage = deserializedError.errorMessage
                        )
                    )
                }
            }
        }.safeAsync {
            // Exception.
            when (it) {
                is IOException -> {
                    UserPartialStatePostSignUp.Error(
                        errorMessage = resourceProvider.getString(R.string.no_internet_connection_error_message)
                    )
                }

                else -> {
                    UserPartialStatePostSignUp.Error(
                        errorMessage = resourceProvider.getString(R.string.unknown_error)
                    )
                }
            }
        }

    override suspend fun postLogIn(userLogInRequest: UserLogInRequest): Flow<UserPartialStatePostLogIn> =
        flow {
            api.postUserLogIn(userLogInRequest).let { response ->
                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        // Success.
                        emit(
                            UserPartialStatePostLogIn.Success(
                                userLogInResponseDomain = responseBody.toDomain()
                            )
                        )
                    } ?: run {
                        // Null body, thus server responded erroneously.
                        emit(
                            UserPartialStatePostLogIn.Error(
                                errorMessage = resourceProvider.getString(R.string.internal_server_error)
                            )
                        )
                    }
                } else {
                    // Error.
                    val deserializedError = gson.fromJson(
                        (response.errorBody() as ResponseBody).string(),
                        UserResponseError::class.java
                    )

                    emit(
                        UserPartialStatePostLogIn.Error(
                            errorMessage = deserializedError.errorMessage
                        )
                    )
                }
            }
        }.safeAsync {
            when (it) {
                is IOException -> {
                    UserPartialStatePostLogIn.Error(
                        errorMessage = resourceProvider.getString(R.string.no_internet_connection_error_message)
                    )
                }

                else -> {
                    UserPartialStatePostLogIn.Error(
                        errorMessage = resourceProvider.getString(R.string.unknown_error)
                    )
                }
            }
        }
}

sealed class UserPartialStatePostSignUp {
    data class Success(val userSignUpResponse: UserSignUpResponse) : UserPartialStatePostSignUp()

    data class Error(val errorMessage: String) : UserPartialStatePostSignUp()
}

sealed class UserPartialStatePostLogIn {
    data class Success(val userLogInResponseDomain: UserLogInResponseDomain) : UserPartialStatePostLogIn()

    data class Error(val errorMessage: String) : UserPartialStatePostLogIn()
}