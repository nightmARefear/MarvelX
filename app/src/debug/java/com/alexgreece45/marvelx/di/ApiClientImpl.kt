package com.alexgreece45.marvelx.di

import com.alexgreece45.marvelx.data.models.character.CharacterDataWrapper
import com.alexgreece45.marvelx.data.api.ApiClient
import com.alexgreece45.marvelx.data.api.ApiService
import com.google.gson.Gson
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Response
import java.io.BufferedReader
import javax.inject.Inject

@Module
@InstallIn(SingletonComponent::class)
class ApiClientImpl @Inject constructor(
    private val api : ApiService,
    private val gson: Gson,
//    @ApplicationContext private val context: ApplicationContext
) : ApiClient {

    override suspend fun getCharacters(): Response<CharacterDataWrapper> {
        // todo: change later to api call
//        return Response.success(
//            200,
//            gson.fromJson(
//                readFileContents("mock_get_characters.json"),
//                CharacterDataWrapper::class.java
//            )
//        )
        return api.getCharacters()
    }

    override suspend fun getCharacter(characterId: Int): Response<CharacterDataWrapper> {
        return api.getCharacter(characterId)
    }

//    private fun readFileContents(fileName: String): BufferedReader {
//        return context.assets.open(fileName).bufferedReader()
//    }
}