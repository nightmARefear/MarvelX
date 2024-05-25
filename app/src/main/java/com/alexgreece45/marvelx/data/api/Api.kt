package com.alexgreece45.marvelx.data.api

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
import com.alexgreece45.marvelx.data.models_api.user.login.RequestUserLogin
import com.alexgreece45.marvelx.data.models_api.user.login.ResponseLogin
import com.alexgreece45.marvelx.data.models_api.user.loginWithToken.ResponseLoginWithToken
import com.alexgreece45.marvelx.data.models_api.user.postLogin.ResponsePostLogin
import com.alexgreece45.marvelx.data.models_api.user.profileDetails.ResponseProfileDetails
import com.alexgreece45.marvelx.data.models_api.user.signup.RequestUserSignup
import com.alexgreece45.marvelx.data.models_api.user.signup.ResponseSignup
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("/v1/public/characters")
    suspend fun getCharacters(): Response<CharacterDataWrapper>

    @GET("/v1/public/characters/{characterId}")
    suspend fun getCharacter(@Path("characterId") characterId: Int): Response<CharacterDataWrapper>

    @GET("/v1/public/characters/{characterId}/comics")
    suspend fun getCharacterComics(@Path("characterId") characterId: Int): Response<ComicDataWrapper>

    @GET("/v1/public/comics/{comicId}")
    suspend fun getComicById(@Path("comicId") comicId: Int): Response<ComicDataWrapper>

    @POST("/api/private/marvelx/user/signup")
    suspend fun signup(@Body requestUserSignup: RequestUserSignup): Response<ResponseSignup>

    @POST("/api/private/marvelx/user/login")
    suspend fun login(@Body requestUserLogin: RequestUserLogin): Response<ResponseLogin>

    @POST("/api/private/marvelx/user/login_with_token")
    suspend fun loginWithToken(): Response<ResponseLoginWithToken>

    @GET("/api/private/marvelx/user/post_login")
    suspend fun postLogin(): Response<ResponsePostLogin>

    @POST("/api/private/marvelx/user/token")
    @FormUrlEncoded
    suspend fun grantNewAccessToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String,
        @Field("grant_type") grantType: String = "authorization_code"
    ): Response<ResponseToken>

    @POST("/api/private/marvelx/user/token")
    @FormUrlEncoded
    suspend fun refreshAccessToken(
        @Field("client_id") clientId: String,
        @Field("refresh_token") refreshToken: String,
        @Field("grant_type") grantType: String = "refresh_token"
    ): Response<ResponseToken>

    @POST("/api/private/marvelx/user/logout")
    @FormUrlEncoded
    suspend fun logout(
        @Field("client_id") clientId: String,
        @Field("refresh_token") refreshToken: String
    ): Response<Void>

    @POST("/api/private/marvelx/user/validate_otp")
    suspend fun validateOtp(@Body otpRequest: OtpRequest): Response<OtpResponseSuccess>

    @POST("/api/private/marvelx/user/resend_otp")
    suspend fun resendOtp(@Body requestResendOtp: RequestResendOtp): Response<ResponseResendOtpSuccess>

    @POST("/api/private/marvelx/user/forgot_password/send_otp")
    suspend fun forgotPasswordSendOtp(@Body body: RequestForgotPasswordSendOtp): Response<ResponseForgotPasswordSendOtp>

    @POST("/api/private/marvelx/user/forgot_password/otp_blocked/email")
    suspend fun forgotPasswordIsOtpBlockedEmail(@Body body: RequestForgotPasswordOtpBlockedEmail): Response<ResponseForgotPasswordOtpBlockedEmail>

    @POST("/api/private/marvelx/user/forgot_password/otp_blocked/phone_number")
    suspend fun forgotPasswordIsOtpBlockedPhoneNumber(@Body body: RequestForgotPasswordOtpBlockedPhoneNumber): Response<ResponseForgotPasswordOtpBlockedPhoneNumber>

    @POST("/api/private/marvelx/user/forgot_password/validate_otp")
    suspend fun forgotPasswordValidateOtp(@Body body: RequestForgotPasswordValidateOtp): Response<ResponseForgotPasswordValidateOtp>

    @GET("/api/private/marvelx/user/profile_details/{userId}")
    suspend fun getUserProfileDetails(@Path("userId") userId: Int): Response<ResponseProfileDetails>

    @POST("/api/private/marvelx/user/jwt_sensitive_data")
    suspend fun getJwtForSensitiveUserData(@Body requestJwtSensitiveUserData: RequestJwtSensitiveUserData): Response<ResponseJwtSensitiveUserData>

    @POST("/api/private/marvelx/user/sensitive_data")
    suspend fun getSensitiveUserData(@Body requestSensitiveUserData: RequestSensitiveUserData): Response<ResponseSensitiveUserData>
}

interface ApiClient {
    suspend fun getCharacters(): Response<CharacterDataWrapper>

    suspend fun getCharacterById(characterId: Int): Response<CharacterDataWrapper>

    suspend fun getCharacterComics(characterId: Int): Response<ComicDataWrapper>

    suspend fun getComicById(comicId: Int): Response<ComicDataWrapper>

    suspend fun signup(requestUserSignup: RequestUserSignup): Response<ResponseSignup>

    suspend fun login(requestUserLogin: RequestUserLogin): Response<ResponseLogin>

    suspend fun loginWithToken(): Response<ResponseLoginWithToken>

    suspend fun postLogin(): Response<ResponsePostLogin>

    suspend fun grantNewAccessToken(
        clientId: String,
        clientSecret: String,
        code: String,
        uri: String
    ): Response<ResponseToken>

    suspend fun refreshAccessToken(clientId: String, refreshToken: String): Response<ResponseToken>

    suspend fun logout(clientId: String, refreshToken: String): Response<Void>

    suspend fun validateOtp(otpRequest: OtpRequest): Response<OtpResponseSuccess>

    suspend fun resendOtp(requestResendOtp: RequestResendOtp): Response<ResponseResendOtpSuccess>

    suspend fun forgotPasswordSendOtp(body: RequestForgotPasswordSendOtp): Response<ResponseForgotPasswordSendOtp>

    suspend fun forgotPasswordIsOtpBlockedEmail(body: RequestForgotPasswordOtpBlockedEmail): Response<ResponseForgotPasswordOtpBlockedEmail>

    suspend fun forgotPasswordIsOtpBlockedPhoneNumber(body: RequestForgotPasswordOtpBlockedPhoneNumber): Response<ResponseForgotPasswordOtpBlockedPhoneNumber>

    suspend fun forgotPasswordValidateOtp(body: RequestForgotPasswordValidateOtp): Response<ResponseForgotPasswordValidateOtp>

    suspend fun getUserProfileDetails(userId: Int): Response<ResponseProfileDetails>

    suspend fun getJwtForSensitiveUserData(requestJwtSensitiveUserData: RequestJwtSensitiveUserData): Response<ResponseJwtSensitiveUserData>

    suspend fun getSensitiveUserData(requestSensitiveUserData: RequestSensitiveUserData): Response<ResponseSensitiveUserData>
}