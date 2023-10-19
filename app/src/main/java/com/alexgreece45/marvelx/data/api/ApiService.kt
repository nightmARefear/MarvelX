package com.alexgreece45.marvelx.data.api

import com.alexgreece45.marvelx.BuildConfig
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
import retrofit2.Response
import retrofit2.http.Body
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
    suspend fun postUserSignup(@Body userSignUpRequest: UserSignupRequest): Response<UserSignupResponse>

    @POST("/api/private/marvelx/user/login")
    suspend fun postUserLogin(@Body userLogInRequest: UserLoginRequest): Response<UserLoginResponse>

    @POST("/api/private/marvelx/user/login_with_token")
    suspend fun postLoginWithToken(@Body userLoginWithTokenRequest: UserLoginWithTokenRequest): Response<UserLoginWithTokenResponse>

    @POST("/api/private/marvelx/user/validate_otp")
    suspend fun validateOtp(@Body otpRequest: OtpRequest): Response<OtpResponseSuccess>

    @GET("/api/private/marvelx/user/profile_details/{userId}")
    suspend fun getUserProfileDetails(@Path("userId") userId: Int): Response<UserProfileDetailsResponse>
}