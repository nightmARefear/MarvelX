package com.alexgreece45.marvelx.repository

import com.alexgreece45.marvelx.data.api.ApiClient
import com.alexgreece45.marvelx.data.models.jwtSensitiveUserData.RequestJwtSensitiveUserData
import com.alexgreece45.marvelx.data.models.jwtSensitiveUserData.ResponseJwtSensitiveUserData
import com.alexgreece45.marvelx.data.models.user.ResponseUserError
import com.alexgreece45.marvelx.data.models.user.login.LoginResponse
import com.alexgreece45.marvelx.data.models.user.login.UserLoginRequest
import com.alexgreece45.marvelx.data.models.user.loginWithToken.LoginWithTokenResponse
import com.alexgreece45.marvelx.data.models.user.loginWithToken.UserLoginWithTokenRequest
import com.alexgreece45.marvelx.data.models.user.signup.SignupResponse
import com.alexgreece45.marvelx.data.models.user.signup.UserSignupRequest
import com.alexgreece45.marvelx.util.helper.parseError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface RegisterRepository {
    suspend fun postLogin(userLoginRequest: UserLoginRequest): Flow<PostLoginRepositoryResponse>
    suspend fun postLoginWithToken(userLoginWithTokenRequest: UserLoginWithTokenRequest): Flow<PostLoginWithTokenRepositoryResponse>
    suspend fun postSignup(userSignupRequest: UserSignupRequest): Flow<PostSignupRepositoryResponse>

    /**
     * Call that retrieves a JWT for accessing sensitive user data (i.e., email, or phone number).
     * This call must be exclusively used in OtpViewModel.
     */
    suspend fun getJwtForSensitiveUserData(requestJwtSensitiveUserData: RequestJwtSensitiveUserData): Flow<GetJwtForSensitiveUserDataRepositoryResponse>
}

class RegisterRepositoryImpl(
    private val apiClient: ApiClient
) : RegisterRepository {
    override suspend fun postLogin(userLoginRequest: UserLoginRequest): Flow<PostLoginRepositoryResponse> =
        flow {
            apiClient.postLogin(userLoginRequest).let { response ->
                if (response.isSuccessful) {
                    emit(
                        PostLoginRepositoryResponse.Success(
                            response = response.body()
                        )
                    )
                } else {
                    emit(
                        PostLoginRepositoryResponse.Failed(
                            responseUserError = parseError(response)
                        )
                    )
                }
            }
        }

    override suspend fun postLoginWithToken(userLoginWithTokenRequest: UserLoginWithTokenRequest): Flow<PostLoginWithTokenRepositoryResponse> =
        flow {
            apiClient.postLoginWithToken(userLoginWithTokenRequest).let { response ->
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

    override suspend fun postSignup(userSignupRequest: UserSignupRequest): Flow<PostSignupRepositoryResponse> =
        flow {
            apiClient.postSignup(userSignupRequest).let { response ->
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

sealed class PostLoginRepositoryResponse {
    data class Success(val response: LoginResponse?) : PostLoginRepositoryResponse()
    data class Failed(val responseUserError: ResponseUserError) : PostLoginRepositoryResponse()
}

sealed class PostLoginWithTokenRepositoryResponse {
    data class Success(val response: LoginWithTokenResponse?) :
        PostLoginWithTokenRepositoryResponse()

    data class Failed(val responseUserError: ResponseUserError) :
        PostLoginWithTokenRepositoryResponse()
}

sealed class PostSignupRepositoryResponse {
    data class Success(val response: SignupResponse?) : PostSignupRepositoryResponse()
    data class Failed(val responseUserError: ResponseUserError) : PostSignupRepositoryResponse()
}

sealed class GetJwtForSensitiveUserDataRepositoryResponse {
    data class Success(val response: ResponseJwtSensitiveUserData?) :
        GetJwtForSensitiveUserDataRepositoryResponse()

    data class Failed(val responseUserError: ResponseUserError) :
        GetJwtForSensitiveUserDataRepositoryResponse()
}