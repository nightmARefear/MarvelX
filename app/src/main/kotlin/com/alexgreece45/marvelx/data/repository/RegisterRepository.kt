package com.alexgreece45.marvelx.data.repository

import com.alexgreece45.marvelx.core.network.clients.ApiClient
import com.alexgreece45.marvelx.core.network.model.api.jwtSensitiveUserData.RequestJwtSensitiveUserData
import com.alexgreece45.marvelx.core.network.model.api.jwtSensitiveUserData.ResponseJwtSensitiveUserData
import com.alexgreece45.marvelx.core.network.model.api.user.ResponseUserError
import com.alexgreece45.marvelx.core.network.model.api.user.login.RequestUserLogin
import com.alexgreece45.marvelx.core.network.model.api.user.login.ResponseLogin
import com.alexgreece45.marvelx.core.network.model.api.user.loginWithToken.ResponseLoginWithToken
import com.alexgreece45.marvelx.core.network.model.api.user.signup.RequestUserSignup
import com.alexgreece45.marvelx.core.network.model.api.user.signup.ResponseSignup
import com.alexgreece45.marvelx.data.repository.helper.parseUserError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Single

interface RegisterRepository {
    suspend fun login(
        email: String,
        password: String,
        isLoggedInWithFirebase: Boolean
    ): Flow<LoginRepositoryResponse>

    suspend fun loginWithToken(): Flow<PostLoginWithTokenRepositoryResponse>
    suspend fun postSignup(requestUserSignup: RequestUserSignup): Flow<SignupRepositoryPartialState>

    /**
     * Call that retrieves a JWT for accessing sensitive user data (i.e., email, or phone number).
     * This call must be exclusively used in OtpViewModel.
     */
    suspend fun getJwtForSensitiveUserData(requestJwtSensitiveUserData: RequestJwtSensitiveUserData): Flow<GetJwtForSensitiveUserDataRepositoryResponse>
}

@Single
internal class RegisterRepositoryImpl(
    private val apiClient: ApiClient
) : RegisterRepository {

    override suspend fun login(
        email: String,
        password: String,
        isLoggedInWithFirebase: Boolean
    ): Flow<LoginRepositoryResponse> =
        flow {
            val requestUserLogin =
                RequestUserLogin(
                    email = email,
                    password = password,
                    isLoggedInWithFirebase = isLoggedInWithFirebase
                )
            apiClient.login(requestUserLogin).let { response ->
                if (response.isSuccessful) {
                    emit(
                        LoginRepositoryResponse.Success(
                            response = response.body()
                        )
                    )
                } else {
                    emit(
                        LoginRepositoryResponse.Failed(
                            responseUserError = parseUserError(response)
                        )
                    )
                }
            }
        }

    override suspend fun loginWithToken(): Flow<PostLoginWithTokenRepositoryResponse> =
        flow {
            apiClient.loginWithToken().let { response ->
                if (response.isSuccessful) {
                    emit(
                        PostLoginWithTokenRepositoryResponse.Success(
                            response = response.body()
                        )
                    )
                } else {
                    emit(
                        PostLoginWithTokenRepositoryResponse.Failed(
                            responseUserError = parseUserError(
                                response
                            )
                        )
                    )
                }
            }
        }

    override suspend fun postSignup(requestUserSignup: RequestUserSignup): Flow<SignupRepositoryPartialState> =
        flow {
            apiClient.signup(requestUserSignup).let { response ->
                if (response.isSuccessful) {
                    emit(
                        SignupRepositoryPartialState.Success(
                            response = response.body()
                        )
                    )
                } else {
                    emit(
                        SignupRepositoryPartialState.Failed(
                            responseUserError = parseUserError(response)
                        )
                    )
                }
            }
        }

    override suspend fun getJwtForSensitiveUserData(requestJwtSensitiveUserData: RequestJwtSensitiveUserData): Flow<GetJwtForSensitiveUserDataRepositoryResponse> =
        flow {
            apiClient.getJwtForSensitiveUserData(requestJwtSensitiveUserData).let { response ->
                if (response.isSuccessful) {
                    emit(
                        GetJwtForSensitiveUserDataRepositoryResponse.Success(
                            response = response.body()
                        )
                    )
                } else {
                    emit(
                        GetJwtForSensitiveUserDataRepositoryResponse.Failed(
                            responseUserError = parseUserError(response)
                        )
                    )
                }
            }
        }
}

sealed class LoginRepositoryResponse {
    data class Success(val response: ResponseLogin?) : LoginRepositoryResponse()
    data class Failed(val responseUserError: ResponseUserError) :
        LoginRepositoryResponse()
}

sealed class PostLoginWithTokenRepositoryResponse {
    data class Success(val response: ResponseLoginWithToken?) :
        PostLoginWithTokenRepositoryResponse()

    data class Failed(val responseUserError: ResponseUserError) :
        PostLoginWithTokenRepositoryResponse()
}

sealed class SignupRepositoryPartialState {
    data class Success(val response: ResponseSignup?) :
        SignupRepositoryPartialState()

    data class Failed(val responseUserError: ResponseUserError) :
        SignupRepositoryPartialState()
}

sealed class GetJwtForSensitiveUserDataRepositoryResponse {
    data class Success(val response: ResponseJwtSensitiveUserData?) :
        GetJwtForSensitiveUserDataRepositoryResponse()

    data class Failed(val responseUserError: ResponseUserError) :
        GetJwtForSensitiveUserDataRepositoryResponse()
}