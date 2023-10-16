package com.alexgreece45.marvelx.data.api

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

interface ApiClient {
    suspend fun getCharacters(): Response<CharacterDataWrapper>

    suspend fun getCharacterById(characterId: Int): Response<CharacterDataWrapper>

    suspend fun getCharacterComics(characterId: Int): Response<ComicDataWrapper>

    suspend fun getComicById(comicId: Int): Response<ComicDataWrapper>

    suspend fun postUserSignup(userSignupRequest: UserSignupRequest): Response<UserSignupResponse>

    suspend fun postUserLogin(userLoginRequest: UserLoginRequest): Response<UserLoginResponse>

    suspend fun postUserLoginWithToken(userLoginWithTokenRequest: UserLoginWithTokenRequest): Response<UserLoginWithTokenResponse>

    suspend fun validateOtp(otpRequest: OtpRequest): Response<OtpResponseSuccess>

    suspend fun getUserProfileDetails(userId: Int): Response<UserProfileDetailsResponse>
}