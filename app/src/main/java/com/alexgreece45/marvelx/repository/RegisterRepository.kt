package com.alexgreece45.marvelx.repository

import com.alexgreece45.marvelx.data.api.ApiClient
import com.alexgreece45.marvelx.data.models_api.jwtSensitiveUserData.RequestJwtSensitiveUserData
import com.alexgreece45.marvelx.data.models_api.jwtSensitiveUserData.ResponseJwtSensitiveUserData
import com.alexgreece45.marvelx.data.models_api.user.ResponseUserError
import com.alexgreece45.marvelx.data.models_api.user.login.ResponseLogin
import com.alexgreece45.marvelx.data.models_api.user.login.RequestUserLogin
import com.alexgreece45.marvelx.data.models_api.user.loginWithToken.ResponseLoginWithToken
import com.alexgreece45.marvelx.data.models_api.user.signup.ResponseSignup
import com.alexgreece45.marvelx.data.models_api.user.signup.RequestUserSignup
import com.alexgreece45.marvelx.util.helper.parseError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface RegisterRepository {
    suspend fun login(requestUserLogin: RequestUserLogin): Flow<LoginRepositoryResponse>
    suspend fun loginWithToken(): Flow<PostLoginWithTokenRepositoryResponse>
    suspend fun postSignup(requestUserSignup: RequestUserSignup): Flow<PostSignupRepositoryResponse>

    /**
     * Call that retrieves a JWT for accessing sensitive user data (i.e., email, or phone number).
     * This call must be exclusively used in OtpViewModel.
     */
    suspend fun getJwtForSensitiveUserData(requestJwtSensitiveUserData: RequestJwtSensitiveUserData): Flow<GetJwtForSensitiveUserDataRepositoryResponse>
}

class RegisterRepositoryImpl(
    private val apiClient: ApiClient
) : RegisterRepository {
    override suspend fun login(requestUserLogin: RequestUserLogin): Flow<LoginRepositoryResponse> =
        flow {
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
                            responseUserError = parseError(response)
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
                            responseUserError = parseError(
                                response
                            )
                        )
                    )
                }
            }
        }

    override suspend fun postSignup(requestUserSignup: RequestUserSignup): Flow<PostSignupRepositoryResponse> =
        flow {
            apiClient.signup(requestUserSignup).let { response ->
                if (response.isSuccessful) {
                    emit(
                        PostSignupRepositoryResponse.Success(
                            response = response.body()
                        )
                    )
                } else {
                    emit(
                        PostSignupRepositoryResponse.Failed(
                            responseUserError = parseError(response)
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
                            responseUserError = parseError(response)
                        )
                    )
                }
            }
        }
}

sealed class LoginRepositoryResponse {
    data class Success(val response: ResponseLogin?) : LoginRepositoryResponse()
    data class Failed(val responseUserError: ResponseUserError) : LoginRepositoryResponse()
}

sealed class PostLoginWithTokenRepositoryResponse {
    data class Success(val response: ResponseLoginWithToken?) :
        PostLoginWithTokenRepositoryResponse()

    data class Failed(val responseUserError: ResponseUserError) :
        PostLoginWithTokenRepositoryResponse()
}

sealed class PostSignupRepositoryResponse {
    data class Success(val response: ResponseSignup?) : PostSignupRepositoryResponse()
    data class Failed(val responseUserError: ResponseUserError) : PostSignupRepositoryResponse()
}

sealed class GetJwtForSensitiveUserDataRepositoryResponse {
    data class Success(val response: ResponseJwtSensitiveUserData?) :
        GetJwtForSensitiveUserDataRepositoryResponse()

    data class Failed(val responseUserError: ResponseUserError) :
        GetJwtForSensitiveUserDataRepositoryResponse()
}