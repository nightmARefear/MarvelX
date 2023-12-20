package com.alexgreece45.marvelx.di

import com.alexgreece45.marvelx.data.api.ApiClient
import com.alexgreece45.marvelx.data.api.ApiService
import com.alexgreece45.marvelx.data.models.character.CharacterDataWrapper
import com.alexgreece45.marvelx.data.models.character.comic.ComicDataWrapper
import com.alexgreece45.marvelx.data.models.forgotPassword.email.RequestForgotPasswordOtpBlockedEmail
import com.alexgreece45.marvelx.data.models.forgotPassword.email.RequestForgotPasswordSendOtpToEmail
import com.alexgreece45.marvelx.data.models.forgotPassword.email.RequestForgotPasswordValidateOtpFromEmail
import com.alexgreece45.marvelx.data.models.forgotPassword.email.ResponseForgotPasswordOtpBlockedEmail
import com.alexgreece45.marvelx.data.models.forgotPassword.email.ResponseForgotPasswordSendOtpToEmail
import com.alexgreece45.marvelx.data.models.forgotPassword.email.ResponseForgotPasswordValidateOtpFromEmail
import com.alexgreece45.marvelx.data.models.forgotPassword.phoneNumber.RequestForgotPasswordOtpBlockedPhoneNumber
import com.alexgreece45.marvelx.data.models.forgotPassword.phoneNumber.RequestForgotPasswordSendOtpToPhoneNumber
import com.alexgreece45.marvelx.data.models.forgotPassword.phoneNumber.RequestForgotPasswordValidateOtpFromPhoneNumber
import com.alexgreece45.marvelx.data.models.forgotPassword.phoneNumber.ResponseForgotPasswordOtpBlockedPhoneNumber
import com.alexgreece45.marvelx.data.models.forgotPassword.phoneNumber.ResponseForgotPasswordSendOtpToPhoneNumber
import com.alexgreece45.marvelx.data.models.forgotPassword.phoneNumber.ResponseForgotPasswordValidateOtpFromPhoneNumber
import com.alexgreece45.marvelx.data.models.jwtSensitiveUserData.RequestJwtSensitiveUserData
import com.alexgreece45.marvelx.data.models.jwtSensitiveUserData.ResponseJwtSensitiveUserData
import com.alexgreece45.marvelx.data.models.otp.OtpRequest
import com.alexgreece45.marvelx.data.models.otp.OtpResponseSuccess
import com.alexgreece45.marvelx.data.models.resendOtp.RequestResendOtp
import com.alexgreece45.marvelx.data.models.resendOtp.ResponseResendOtpSuccess
import com.alexgreece45.marvelx.data.models.sensitiveUserData.RequestSensitiveUserData
import com.alexgreece45.marvelx.data.models.sensitiveUserData.ResponseSensitiveUserData
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
    private val apiService: ApiService
) : ApiClient {

    override suspend fun getCharacters(): Response<CharacterDataWrapper> =
        apiService.getCharacters()

    override suspend fun getCharacterById(characterId: Int): Response<CharacterDataWrapper> =
        apiService.getCharacter(characterId)

    override suspend fun getCharacterComics(characterId: Int): Response<ComicDataWrapper> =
        apiService.getCharacterComics(characterId)

    override suspend fun getComicById(comicId: Int): Response<ComicDataWrapper> =
        apiService.getComicById(comicId)

    override suspend fun postSignup(userSignupRequest: UserSignupRequest): Response<SignupResponse> =
        apiService.postSignup(userSignupRequest)

    override suspend fun postLogin(userLoginRequest: UserLoginRequest): Response<LoginResponse> =
        apiService.postLogin(userLoginRequest)

    override suspend fun postLoginWithToken(userLoginWithTokenRequest: UserLoginWithTokenRequest): Response<LoginWithTokenResponse> =
        apiService.postLoginWithToken(userLoginWithTokenRequest)

    override suspend fun grantNewAccessToken(
        clientId: String,
        clientSecret: String,
        code: String,
        uri: String
    ): Response<TokenResponse> = apiService.grantNewAccessToken(clientId, clientSecret, code, uri)

    override suspend fun refreshAccessToken(
        clientId: String,
        refreshToken: String
    ): Response<TokenResponse> = apiService.refreshAccessToken(clientId, refreshToken)

    override suspend fun logout(clientId: String, refreshToken: String): Response<Void> =
        apiService.logout(clientId, refreshToken)

    override suspend fun validateOtp(otpRequest: OtpRequest): Response<OtpResponseSuccess> =
        apiService.validateOtp(otpRequest)

    override suspend fun resendOtp(requestResendOtp: RequestResendOtp): Response<ResponseResendOtpSuccess> =
        apiService.resendOtp(requestResendOtp)

    override suspend fun forgotPasswordSendOtpToEmail(body: RequestForgotPasswordSendOtpToEmail): Response<ResponseForgotPasswordSendOtpToEmail> =
        apiService.forgotPasswordSendOtpToEmail(body)

    override suspend fun forgotPasswordSendOtpToPhoneNumber(body: RequestForgotPasswordSendOtpToPhoneNumber): Response<ResponseForgotPasswordSendOtpToPhoneNumber> =
        apiService.forgotPasswordSendOtpToPhoneNumber(body)

    override suspend fun forgotPasswordIsOtpBlockedEmail(body: RequestForgotPasswordOtpBlockedEmail): Response<ResponseForgotPasswordOtpBlockedEmail> =
        apiService.forgotPasswordIsOtpBlockedEmail(body)

    override suspend fun forgotPasswordIsOtpBlockedPhoneNumber(body: RequestForgotPasswordOtpBlockedPhoneNumber): Response<ResponseForgotPasswordOtpBlockedPhoneNumber> =
        apiService.forgotPasswordIsOtpBlockedPhoneNumber(body)

    override suspend fun forgotPasswordValidateOtpFromEmail(body: RequestForgotPasswordValidateOtpFromEmail): Response<ResponseForgotPasswordValidateOtpFromEmail> =
        apiService.forgotPasswordValidateOtpFromEmail(body)

    override suspend fun forgotPasswordValidateOtpFromPhoneNumber(body: RequestForgotPasswordValidateOtpFromPhoneNumber): Response<ResponseForgotPasswordValidateOtpFromPhoneNumber> =
        apiService.forgotPasswordValidateOtpFromPhoneNumber(body)

    override suspend fun getUserProfileDetails(userId: Int): Response<ResponseProfileDetails> =
        apiService.getUserProfileDetails(userId = userId)

    override suspend fun getJwtForSensitiveUserData(requestJwtSensitiveUserData: RequestJwtSensitiveUserData): Response<ResponseJwtSensitiveUserData> =
        apiService.getJwtForSensitiveUserData(requestJwtSensitiveUserData)

    override suspend fun getSensitiveUserData(requestSensitiveUserData: RequestSensitiveUserData): Response<ResponseSensitiveUserData> =
        apiService.getSensitiveUserData(requestSensitiveUserData)
}