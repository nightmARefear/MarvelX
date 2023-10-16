package com.alexgreece45.marvelx.di

import com.alexgreece45.marvelx.data.api.ApiClient
import com.alexgreece45.marvelx.data.api.ApiService
import com.alexgreece45.marvelx.data.models.character.CharacterDataWrapper
import com.alexgreece45.marvelx.data.models.character.comic.ComicDataWrapper
import com.alexgreece45.marvelx.data.models.otp.OtpRequest
import com.alexgreece45.marvelx.data.models.otp.OtpResponseSuccess
import com.alexgreece45.marvelx.data.models.user.login.UserLoginRequest
import com.alexgreece45.marvelx.data.models.user.login.UserLoginResponse
import com.alexgreece45.marvelx.data.models.user.loginWithToken.UserLoginWithTokenRequest
import com.alexgreece45.marvelx.data.models.user.loginWithToken.UserLoginWithTokenResponse
import com.alexgreece45.marvelx.data.models.user.profileDetails.UserProfileDetailsResponse
import com.alexgreece45.marvelx.data.models.user.signUp.UserSignupRequest
import com.alexgreece45.marvelx.data.models.user.signUp.UserSignupResponse
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Response
import javax.inject.Inject

@Module
@InstallIn(SingletonComponent::class)
class ApiClientImpl @Inject constructor(
    private val api: ApiService
) : ApiClient {

    override suspend fun getCharacters(): Response<CharacterDataWrapper> =
        api.getCharacters()

    override suspend fun getCharacterById(characterId: Int): Response<CharacterDataWrapper> =
        api.getCharacter(characterId)

    override suspend fun getCharacterComics(characterId: Int): Response<ComicDataWrapper> =
        api.getCharacterComics(characterId)

    override suspend fun getComicById(comicId: Int): Response<ComicDataWrapper> =
        api.getComicById(comicId)

    override suspend fun postUserSignup(userSignupRequest: UserSignupRequest): Response<UserSignupResponse> =
        api.postUserSignup(userSignupRequest)

    override suspend fun postUserLogin(userLoginRequest: UserLoginRequest): Response<UserLoginResponse> =
        api.postUserLogin(userLoginRequest)

    override suspend fun postUserLoginWithToken(userLoginWithTokenRequest: UserLoginWithTokenRequest): Response<UserLoginWithTokenResponse> =
        api.postLoginWithToken(userLoginWithTokenRequest)

    override suspend fun validateOtp(otpRequest: OtpRequest): Response<OtpResponseSuccess> =
        api.validateOtp(otpRequest)

    override suspend fun getUserProfileDetails(userId: Int): Response<UserProfileDetailsResponse> =
        api.getUserProfileDetails(userId = userId)
}

sealed interface ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>
    data class Error(
        val errorCode: Int? = null,
        val errorMessage: String
    ) : ApiResult<Nothing>

    data class NoInternetConnection(
        val errorMessage: String
    ) : ApiResult<Nothing>
}