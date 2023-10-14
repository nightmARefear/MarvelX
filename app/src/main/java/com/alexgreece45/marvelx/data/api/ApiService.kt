package com.alexgreece45.marvelx.data.api

import com.alexgreece45.marvelx.BuildConfig
import com.alexgreece45.marvelx.data.models.character.CharacterDataWrapper
import com.alexgreece45.marvelx.data.models.character.comic.ComicDataWrapper
import com.alexgreece45.marvelx.data.models.otp.OtpRequest
import com.alexgreece45.marvelx.data.models.otp.OtpResponseSuccess
import com.alexgreece45.marvelx.data.models.user.logIn.UserLogInRequest
import com.alexgreece45.marvelx.data.models.user.logIn.UserLoginResponse
import com.alexgreece45.marvelx.data.models.user.profileDetails.UserProfileDetailsResponse
import com.alexgreece45.marvelx.data.models.user.signUp.UserSignUpRequest
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

    @POST(BuildConfig.MARVELX_BACKEND_ENDPOINT_POST_SIGN_UP)
    suspend fun postSignUp(@Body userSignUpRequest: UserSignUpRequest): Response<UserSignupResponse>

    @POST(BuildConfig.MARVELX_BACKEND_ENDPOINT_POST_LOG_IN)
    suspend fun postLogIn(@Body userLogInRequest: UserLogInRequest): Response<UserLoginResponse>

    @POST(BuildConfig.MARVELX_BACKEND_ENDPOINT_POST_OTP)
    suspend fun validateOtp(@Body otpRequest: OtpRequest): Response<OtpResponseSuccess>

    @GET("${BuildConfig.MARVELX_BACKEND_ENDPOINT_GET_PROFILE_DETAILS}/{userId}")
    suspend fun getUserProfileDetails(@Path("userId") userId: Int): Response<UserProfileDetailsResponse>
}