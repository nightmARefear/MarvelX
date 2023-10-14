package com.alexgreece45.marvelx.data.api

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

interface ApiClient {
    suspend fun getCharacters(): Response<CharacterDataWrapper>

    suspend fun getCharacterById(characterId: Int): Response<CharacterDataWrapper>

    suspend fun getCharacterComics(characterId: Int): Response<ComicDataWrapper>

    suspend fun getComicById(comicId: Int): Response<ComicDataWrapper>

    suspend fun postSignUp(userSignUpRequest: UserSignUpRequest): Response<UserSignupResponse>

    suspend fun postUserLogIn(userLogInRequest: UserLogInRequest): Response<UserLoginResponse>

    suspend fun validateOtp(otpRequest: OtpRequest): Response<OtpResponseSuccess>

    suspend fun getUserProfileDetails(userId: Int): Response<UserProfileDetailsResponse>
}