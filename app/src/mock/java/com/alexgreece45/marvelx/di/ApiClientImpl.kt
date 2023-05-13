package com.alexgreece45.marvelx.di

import android.content.Context
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
    private val api: ApiService,
    @ApplicationContext private val context: Context,
    private val gson: Gson
) : ApiClient {

    override suspend fun getCharacters(): Response<CharacterDataWrapper> {
        return Response.success(
            200,
            gson.fromJson(
                readFileContents("mock_get_characters.json"),
                CharacterDataWrapper::class.java
            )
        )
    }

    private fun readFileContents(fileName: String): BufferedReader {
        return context.assets.open(fileName).bufferedReader()
    }
}