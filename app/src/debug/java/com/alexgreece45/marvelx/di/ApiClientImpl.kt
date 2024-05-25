package com.alexgreece45.marvelx.di

import com.alexgreece45.marvelx.data.api.ApiClient
import com.alexgreece45.marvelx.data.api.ApiService
import com.alexgreece45.marvelx.data.models_api.character.CharacterDataWrapper
import com.alexgreece45.marvelx.data.models_api.character.comic.ComicDataWrapper
import com.alexgreece45.marvelx.data.models_api.forgotPassword.email.RequestForgotPasswordOtpBlockedEmail
import com.alexgreece45.marvelx.data.models_api.forgotPassword.email.RequestForgotPasswordSendOtp
import com.alexgreece45.marvelx.data.models_api.forgotPassword.email.RequestForgotPasswordValidateOtp
import com.alexgreece45.marvelx.data.models_api.forgotPassword.email.ResponseForgotPasswordOtpBlockedEmail
import com.alexgreece45.marvelx.data.models_api.forgotPassword.email.ResponseForgotPasswordSendOtp
import com.alexgreece45.marvelx.data.models_api.forgotPassword.email.ResponseForgotPasswordValidateOtp
import com.alexgreece45.marvelx.data.models_api.forgotPassword.phoneNumber.RequestForgotPasswordOtpBlockedPhoneNumber
import com.alexgreece45.marvelx.data.models_api.forgotPassword.phoneNumber.ResponseForgotPasswordOtpBlockedPhoneNumber
import com.alexgreece45.marvelx.data.models_api.jwtSensitiveUserData.RequestJwtSensitiveUserData
import com.alexgreece45.marvelx.data.models_api.jwtSensitiveUserData.ResponseJwtSensitiveUserData
import com.alexgreece45.marvelx.data.models_api.otp.OtpRequest
import com.alexgreece45.marvelx.data.models_api.otp.OtpResponseSuccess
import com.alexgreece45.marvelx.data.models_api.resendOtp.RequestResendOtp
import com.alexgreece45.marvelx.data.models_api.resendOtp.ResponseResendOtpSuccess
import com.alexgreece45.marvelx.data.models_api.sensitiveUserData.RequestSensitiveUserData
import com.alexgreece45.marvelx.data.models_api.sensitiveUserData.ResponseSensitiveUserData
import com.alexgreece45.marvelx.data.models_api.token.ResponseToken
import com.alexgreece45.marvelx.data.models_api.user.login.ResponseLogin
import com.alexgreece45.marvelx.data.models_api.user.login.RequestUserLogin
import com.alexgreece45.marvelx.data.models_api.user.loginWithToken.ResponseLoginWithToken
import com.alexgreece45.marvelx.data.models_api.user.postLogin.ResponsePostLogin
import com.alexgreece45.marvelx.data.models_api.user.profileDetails.ResponseProfileDetails
import com.alexgreece45.marvelx.data.models_api.user.signup.ResponseSignup
import com.alexgreece45.marvelx.data.models_api.user.signup.RequestUserSignup
import retrofit2.Response

class ApiClientImpl(
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

    override suspend fun signup(requestUserSignup: RequestUserSignup): Response<ResponseSignup> =
        apiService.signup(requestUserSignup)

    override suspend fun login(requestUserLogin: RequestUserLogin): Response<ResponseLogin> =
        apiService.login(requestUserLogin)

    override suspend fun loginWithToken(): Response<ResponseLoginWithToken> =
        apiService.loginWithToken()

    override suspend fun postLogin(): Response<ResponsePostLogin> =
        apiService.postLogin()

    override suspend fun grantNewAccessToken(
        clientId: String,
        clientSecret: String,
        code: String,
        uri: String
    ): Response<ResponseToken> = apiService.grantNewAccessToken(clientId, clientSecret, code, uri)

    override suspend fun refreshAccessToken(
        clientId: String,
        refreshToken: String
    ): Response<ResponseToken> = apiService.refreshAccessToken(clientId, refreshToken)

    override suspend fun logout(clientId: String, refreshToken: String): Response<Void> =
        apiService.logout(clientId, refreshToken)

    override suspend fun validateOtp(otpRequest: OtpRequest): Response<OtpResponseSuccess> =
        apiService.validateOtp(otpRequest)

    override suspend fun resendOtp(requestResendOtp: RequestResendOtp): Response<ResponseResendOtpSuccess> =
        apiService.resendOtp(requestResendOtp)

    override suspend fun forgotPasswordSendOtp(body: RequestForgotPasswordSendOtp): Response<ResponseForgotPasswordSendOtp> =
        apiService.forgotPasswordSendOtp(body)

    override suspend fun forgotPasswordIsOtpBlockedEmail(body: RequestForgotPasswordOtpBlockedEmail): Response<ResponseForgotPasswordOtpBlockedEmail> =
        apiService.forgotPasswordIsOtpBlockedEmail(body)

    override suspend fun forgotPasswordIsOtpBlockedPhoneNumber(body: RequestForgotPasswordOtpBlockedPhoneNumber): Response<ResponseForgotPasswordOtpBlockedPhoneNumber> =
        apiService.forgotPasswordIsOtpBlockedPhoneNumber(body)

    override suspend fun forgotPasswordValidateOtp(body: RequestForgotPasswordValidateOtp): Response<ResponseForgotPasswordValidateOtp> =
        apiService.forgotPasswordValidateOtp(body)

    override suspend fun getUserProfileDetails(userId: Int): Response<ResponseProfileDetails> =
        apiService.getUserProfileDetails(userId = userId)

    override suspend fun getJwtForSensitiveUserData(requestJwtSensitiveUserData: RequestJwtSensitiveUserData): Response<ResponseJwtSensitiveUserData> =
        apiService.getJwtForSensitiveUserData(requestJwtSensitiveUserData)

    override suspend fun getSensitiveUserData(requestSensitiveUserData: RequestSensitiveUserData): Response<ResponseSensitiveUserData> =
        apiService.getSensitiveUserData(requestSensitiveUserData)
}