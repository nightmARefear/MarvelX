package com.alexgreece45.marvelx.di

import com.alexgreece45.marvelx.data.api.ApiClient
import com.alexgreece45.marvelx.data.api.ApiService
import com.alexgreece45.marvelx.data.models.character.CharacterDataWrapper
import com.alexgreece45.marvelx.data.models.character.comic.ComicDataWrapper
import com.alexgreece45.marvelx.data.models.otp.OtpRequest
import com.alexgreece45.marvelx.data.models.otp.OtpResponseSuccess
import com.alexgreece45.marvelx.data.models.user.logIn.UserLogInRequest
import com.alexgreece45.marvelx.data.models.user.logIn.UserLogInResponse
import com.alexgreece45.marvelx.data.models.user.signUp.UserSignUpRequest
import com.alexgreece45.marvelx.data.models.user.signUp.UserSignUpResponse
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

    override suspend fun getCharacters(): Response<CharacterDataWrapper> {
        return api.getCharacters()
    }

    override suspend fun getCharacterById(characterId: Int): Response<CharacterDataWrapper> {
        return api.getCharacter(characterId)
    }

    override suspend fun getCharacterComics(characterId: Int): Response<ComicDataWrapper> {
        return api.getCharacterComics(characterId)
    }

    override suspend fun getComicById(comicId: Int): Response<ComicDataWrapper> {
        return api.getComicById(comicId)
    }

    override suspend fun postSignUp(userSignUpRequest: UserSignUpRequest): Response<UserSignUpResponse> {
        return api.postSignUp(userSignUpRequest)
    }

    override suspend fun postUserLogIn(userLogInRequest: UserLogInRequest): Response<UserLogInResponse> {
        return api.postLogIn(userLogInRequest)
    }

    override suspend fun postOtp(otpRequest: OtpRequest): Response<OtpResponseSuccess> {
        return api.getOtp(otpRequest)
    }
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