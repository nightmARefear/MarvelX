package com.alexgreece45.marvelx.di

import android.content.Context
import com.alexgreece45.marvelx.data.api.ApiClient
import com.alexgreece45.marvelx.data.models.character.CharacterDataContainer
import com.alexgreece45.marvelx.data.models.character.CharacterDataWrapper
import com.alexgreece45.marvelx.data.models.character.comic.ComicDataContainer
import com.alexgreece45.marvelx.data.models.character.comic.ComicDataWrapper
import com.google.gson.Gson
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import java.io.BufferedReader
import javax.inject.Inject

@Module
@InstallIn(SingletonComponent::class)
class ApiClientImpl @Inject constructor(
    private val context: Context,
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

    override suspend fun getCharacterById(characterId: Int): Response<CharacterDataWrapper> =
        try {
            val response = gson.fromJson(
                readFileContents("mock_get_characters.json"),
                CharacterDataWrapper::class.java
            )
            val character = response.data.results.find { it.id == characterId }
            if (character == null) {
                Response.error<String>(
                    404,
                    "Character with id \"$characterId\" was not found in mock_get_characters.json.".toResponseBody()
                )
            }

            Response.success(
                200,
                CharacterDataWrapper(
                    code = 0,
                    status = "",
                    copyright = "",
                    attributionText = "",
                    attributionHTML = "",
                    data = CharacterDataContainer(
                        offset = 0,
                        limit = 0,
                        total = 0,
                        count = 0,
                        results = listOf(character!!)
                    ),
                    etag = ""
                )
            )
        } catch (e: Exception) {
            Response.error(
                500,
                "An error occurred while parsing \"mock_get_characters.json\".".toResponseBody()
            )
        }


    /**
     * Gets the comics for a specific characterId.
     *
     * ### Note: Since this is the mock version, the comics for the character "A-Bomb (HAS)" are always returned.
     *
     * @param characterId the character's id
     */
    override suspend fun getCharacterComics(characterId: Int): Response<ComicDataWrapper> =
        try {
            val response = gson.fromJson(
                readFileContents("mock_get_character_comics.json"),
                ComicDataWrapper::class.java
            )

            Response.success(
                200,
                ComicDataWrapper(
                    code = 0,
                    status = "",
                    copyright = "",
                    attributionText = "",
                    attributionHTML = "",
                    data = ComicDataContainer(
                        offset = 0,
                        limit = 0,
                        total = 0,
                        count = 0,
                        results = response.data.results
                    ),
                    etag = ""

                )
            )
        } catch (e: Exception) {
            Response.error(
                500,
                "An error occurred while parsing \"mock_get_character_comics.json\".".toResponseBody()
            )
        }

    override suspend fun getComicById(comicId: Int): Response<ComicDataWrapper> =
        // Arbitrary characterId value, since no search operation is executed.
        getCharacterComics(0).let { comics ->
            return when (comics.isSuccessful) {
                false -> {
                    Response.error(404, comics.errorBody() ?: "".toResponseBody())
                }

                true -> {
                    val comic = comics.body()?.data?.results?.find { it.id == comicId }
                    if (comic == null) Response.error<String>(
                        404,
                        "Comic with id \"$comicId\" was not found in mock_get_character_comics.json.".toResponseBody()
                    )

                    Response.success(
                        200,
                        ComicDataWrapper(
                            code = 0,
                            status = "",
                            copyright = "",
                            attributionText = "",
                            attributionHTML = "",
                            data = ComicDataContainer(
                                offset = 0,
                                limit = 0,
                                total = 0,
                                count = 0,
                                results = listOf(comic!!)
                            ),
                            etag = ""
                        )
                    )
                }
            }
        }


    private fun readFileContents(fileName: String): BufferedReader {
        return context.assets.open(fileName).bufferedReader()
    }
}