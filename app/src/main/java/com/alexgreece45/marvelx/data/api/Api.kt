package com.alexgreece45.marvelx.data.api

import com.alexgreece45.marvelx.data.models.character.CharacterDataWrapper
import com.alexgreece45.marvelx.data.models.character.comic.ComicDataWrapper
import com.alexgreece45.marvelx.data.models.otp.OtpRequest
import com.alexgreece45.marvelx.data.models.otp.OtpResponseSuccess
import com.alexgreece45.marvelx.data.models.token.TokenResponse
import com.alexgreece45.marvelx.data.models.user.login.UserLoginRequest
import com.alexgreece45.marvelx.data.models.user.login.LoginResponse
import com.alexgreece45.marvelx.data.models.user.loginWithToken.UserLoginWithTokenRequest
import com.alexgreece45.marvelx.data.models.user.loginWithToken.LoginWithTokenResponse
import com.alexgreece45.marvelx.data.models.user.profileDetails.ResponseProfileDetails
import com.alexgreece45.marvelx.data.models.user.signup.UserSignupRequest
import com.alexgreece45.marvelx.data.models.user.signup.SignupResponse
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
    suspend fun postSignup(@Body userSignUpRequest: UserSignupRequest): Response<SignupResponse>

    @POST("/api/private/marvelx/user/login")
    suspend fun postLogin(@Body userLogInRequest: UserLoginRequest): Response<LoginResponse>

    @POST("/api/private/marvelx/user/login_with_token")
    suspend fun postLoginWithToken(@Body userLoginWithTokenRequest: UserLoginWithTokenRequest): Response<LoginWithTokenResponse>

    @POST("/api/private/marvelx/user/token")
    @FormUrlEncoded
    suspend fun grantNewAccessToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String,
        @Field("grant_type") grantType: String = "authorization_code"
    ): Response<TokenResponse>

    @POST("/api/private/marvelx/user/token")
    @FormUrlEncoded
    suspend fun refreshAccessToken(
        @Field("client_id") clientId: String,
        @Field("refresh_token") refreshToken: String,
        @Field("grant_type") grantType: String = "refresh_token"
    ): Response<TokenResponse>

    @POST("/api/private/marvelx/user/logout")
    @FormUrlEncoded
    suspend fun logout(
        @Field("client_id") clientId: String,
        @Field("refresh_token") refreshToken: String
    ): Response<Void>

    @POST("/api/private/marvelx/user/validate_otp")
    suspend fun validateOtp(@Body otpRequest: OtpRequest): Response<OtpResponseSuccess>

    @GET("/api/private/marvelx/user/profile_details/{userId}")
    suspend fun getUserProfileDetails(@Path("userId") userId: Int): Response<ResponseProfileDetails>
}

interface ApiClient {
    suspend fun getCharacters(): Response<CharacterDataWrapper>

    suspend fun getCharacterById(characterId: Int): Response<CharacterDataWrapper>

    suspend fun getCharacterComics(characterId: Int): Response<ComicDataWrapper>

    suspend fun getComicById(comicId: Int): Response<ComicDataWrapper>

    suspend fun postSignup(userSignupRequest: UserSignupRequest): Response<SignupResponse>

    suspend fun postLogin(userLoginRequest: UserLoginRequest): Response<LoginResponse>

    suspend fun postLoginWithToken(userLoginWithTokenRequest: UserLoginWithTokenRequest): Response<LoginWithTokenResponse>

    suspend fun grantNewAccessToken(clientId: String, clientSecret: String, code: String, uri: String): Response<TokenResponse>

    suspend fun refreshAccessToken(clientId: String, refreshToken: String): Response<TokenResponse>

    suspend fun logout(clientId: String, refreshToken: String): Response<Void>

    suspend fun validateOtp(otpRequest: OtpRequest): Response<OtpResponseSuccess>

    suspend fun getUserProfileDetails(userId: Int): Response<ResponseProfileDetails>
}