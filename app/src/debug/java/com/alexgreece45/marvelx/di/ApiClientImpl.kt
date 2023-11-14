package com.alexgreece45.marvelx.di

import com.alexgreece45.marvelx.data.api.ApiClient
import com.alexgreece45.marvelx.data.api.ApiService
import com.alexgreece45.marvelx.data.models.character.CharacterDataWrapper
import com.alexgreece45.marvelx.data.models.character.comic.ComicDataWrapper
import com.alexgreece45.marvelx.data.models.otp.OtpRequest
import com.alexgreece45.marvelx.data.models.otp.OtpResponseSuccess
import com.alexgreece45.marvelx.data.models.token.TokenResponse
import com.alexgreece45.marvelx.data.models.user.login.LoginResponse
import com.alexgreece45.marvelx.data.models.user.login.UserLoginRequest
import com.alexgreece45.marvelx.data.models.user.loginWithToken.LoginWithTokenResponse
import com.alexgreece45.marvelx.data.models.user.loginWithToken.UserLoginWithTokenRequest
import com.alexgreece45.marvelx.data.models.user.profileDetails.ResponseProfileDetails
import com.alexgreece45.marvelx.data.models.user.signup.SignupResponse
import com.alexgreece45.marvelx.data.models.user.signup.UserSignupRequest
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

    override suspend fun postSignup(userSignupRequest: UserSignupRequest): Response<SignupResponse> =
        api.postSignup(userSignupRequest)

    override suspend fun postLogin(userLoginRequest: UserLoginRequest): Response<LoginResponse> =
        api.postLogin(userLoginRequest)

    override suspend fun postLoginWithToken(userLoginWithTokenRequest: UserLoginWithTokenRequest): Response<LoginWithTokenResponse> =
        api.postLoginWithToken(userLoginWithTokenRequest)

    override suspend fun grantNewAccessToken(
        clientId: String,
        clientSecret: String,
        code: String,
        uri: String
    ): Response<TokenResponse> = api.grantNewAccessToken(clientId, clientSecret, code, uri)

    override suspend fun refreshAccessToken(
        clientId: String,
        refreshToken: String
    ): Response<TokenResponse> = api.refreshAccessToken(clientId, refreshToken)

    override suspend fun logout(clientId: String, refreshToken: String): Response<Void> =
        api.logout(clientId, refreshToken)

    override suspend fun validateOtp(otpRequest: OtpRequest): Response<OtpResponseSuccess> =
        api.validateOtp(otpRequest)

    override suspend fun getUserProfileDetails(userId: Int): Response<ResponseProfileDetails> =
        api.getUserProfileDetails(userId = userId)
}