package com.alexgreece45.marvelx.domain.interactors

import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.data.api.ApiClient
import com.alexgreece45.marvelx.data.domains.user.login.UserLogInResponseDomain
import com.alexgreece45.marvelx.data.domains.user.profileDetails.UserProfileDetailsDomain
import com.alexgreece45.marvelx.data.models.user.UserResponseError
import com.alexgreece45.marvelx.data.models.user.logIn.UserLogInRequest
import com.alexgreece45.marvelx.data.models.user.logIn.toDomain
import com.alexgreece45.marvelx.data.models.user.profileDetails.UserProfileDetailsResponse
import com.alexgreece45.marvelx.data.models.user.profileDetails.toUserProfileDetailsDomain
import com.alexgreece45.marvelx.data.models.user.signUp.UserSignUpRequest
import com.alexgreece45.marvelx.data.models.user.signUp.UserSignupResponse
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

    suspend fun getProfileDetails(userId: Int): Flow<UserPartialStateGetProfileDetails>
}

class UserInteractorImpl @Inject constructor(
    private val apiClient: ApiClient,
    private val resourceProvider: ResourceProvider,
    private val gson: Gson
) : UserInteractor {
    override suspend fun postSignUp(userSignUpRequest: UserSignUpRequest): Flow<UserPartialStatePostSignUp> =
        flow {
            apiClient.postSignUp(userSignUpRequest).let { response ->
                if (response.isSuccessful) {
                    // Success.
                    response.body()?.let { userSignupResponse ->
                        emit(
                            UserPartialStatePostSignUp.Success(
                                userSignUpResponse = userSignupResponse
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
                        errorMessage = resourceProvider.getGenericErrorMessage()
                    )
                }
            }
        }

    override suspend fun postLogIn(userLogInRequest: UserLogInRequest): Flow<UserPartialStatePostLogIn> =
        flow {
            apiClient.postUserLogIn(userLogInRequest).let { response ->
                if (response.isSuccessful) {
                    response.body()?.let { userLoginResponse ->
                        // Success.
                        emit(
                            UserPartialStatePostLogIn.Success(
                                userLogInResponseDomain = userLoginResponse.toDomain()
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
                        errorMessage = resourceProvider.getGenericErrorMessage()
                    )
                }
            }
        }

    override suspend fun getProfileDetails(userId: Int): Flow<UserPartialStateGetProfileDetails> =
        flow {
            apiClient.getUserProfileDetails(userId = userId).let { response ->
                if (response.isSuccessful) {
                    response.body()?.let { userProfileDetailsResponse ->
                        emit(
                            UserPartialStateGetProfileDetails.Success(
                                profileDetails = userProfileDetailsResponse.toUserProfileDetailsDomain()
                            )
                        )
                    } ?: run {
                        emit(
                            UserPartialStateGetProfileDetails.Error(
                                errorMessage = resourceProvider.getString(R.string.internal_server_error)
                            )
                        )
                    }
                } else {
                    emit(
                        UserPartialStateGetProfileDetails.Error(
                            errorMessage = resourceProvider.getGenericErrorMessage()
                        )
                    )
                }
            }
        }.safeAsync {
            when (it) {
                is IOException -> UserPartialStateGetProfileDetails.NoInternetConnection(
                    errorMessage = resourceProvider.getString(R.string.no_internet_connection_error_message)
                )

                else -> UserPartialStateGetProfileDetails.Error(
                    errorMessage = resourceProvider.getGenericErrorMessage()
                )
            }
        }
}

sealed class UserPartialStatePostSignUp {
    data class Success(val userSignUpResponse: UserSignupResponse) : UserPartialStatePostSignUp()

    data class Error(val errorMessage: String) : UserPartialStatePostSignUp()
}

sealed class UserPartialStatePostLogIn {
    data class Success(val userLogInResponseDomain: UserLogInResponseDomain) :
        UserPartialStatePostLogIn()

    data class Error(val errorMessage: String) : UserPartialStatePostLogIn()
}

sealed class UserPartialStateGetProfileDetails {
    data class Success(val profileDetails: UserProfileDetailsDomain) :
        UserPartialStateGetProfileDetails()

    data class Error(val errorMessage: String) : UserPartialStateGetProfileDetails()

    data class NoInternetConnection(val errorMessage: String) : UserPartialStateGetProfileDetails()
}