package com.alexgreece45.marvelx.repository

import com.alexgreece45.marvelx.data.api.ApiClient
import com.alexgreece45.marvelx.data.models.character.CharacterDataWrapper
import com.alexgreece45.marvelx.data.models.character.comic.ComicDataWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface CharacterRepository {
    suspend fun getAllCharacters(): Flow<GetAllCharactersRepositoryResponse>
    suspend fun getCharacter(characterId: Int): Flow<GetCharacterRepositoryResponse>
    suspend fun getCharacterComics(characterId: Int): Flow<GetCharacterComicsRepositoryResponse>
}

class CharacterRepositoryImpl(
    private val apiClient: ApiClient
) : CharacterRepository {
    override suspend fun getAllCharacters(): Flow<GetAllCharactersRepositoryResponse> = flow {
        apiClient.getCharacters().let { response ->
            if (response.isSuccessful) {
                emit(
                    GetAllCharactersRepositoryResponse.Success(
                        response = response.body()
                    )
                )
            } else {
                emit(
                    GetAllCharactersRepositoryResponse.Failed(
                        errorMessage = response.errorBody()?.string()
                    )
                )
            }
        }
    }

    override suspend fun getCharacter(characterId: Int): Flow<GetCharacterRepositoryResponse> =
        flow {
            apiClient.getCharacterById(characterId).let { response ->
                if (response.isSuccessful) {
                    emit(
                        GetCharacterRepositoryResponse.Success(
                            response = response.body()
                        )
                    )
                } else {
                    emit(
                        GetCharacterRepositoryResponse.Failed(
                            errorMessage = response.errorBody()?.string()
                        )
                    )
                }
            }
        }

    override suspend fun getCharacterComics(characterId: Int): Flow<GetCharacterComicsRepositoryResponse> =
        flow {
            apiClient.getCharacterComics(characterId).let { response ->
                if (response.isSuccessful) {
                    emit(
                        GetCharacterComicsRepositoryResponse.Success(
                            response = response.body()
                        )
                    )
                } else {
                    emit(
                        GetCharacterComicsRepositoryResponse.Failed(
                            errorMessage = response.errorBody()?.string()
                        )
                    )
                }
            }
        }
}

sealed class GetCharacterComicsRepositoryResponse {
    data class Success(val response: ComicDataWrapper?) : GetCharacterComicsRepositoryResponse()
    data class Failed(val errorMessage: String?) : GetCharacterComicsRepositoryResponse()
}

sealed class GetAllCharactersRepositoryResponse {
    data class Success(val response: CharacterDataWrapper?) : GetAllCharactersRepositoryResponse()
    data class Failed(val errorMessage: String?) : GetAllCharactersRepositoryResponse()
}

sealed class GetCharacterRepositoryResponse {
    data class Success(val response: CharacterDataWrapper?) : GetCharacterRepositoryResponse()
    data class Failed(val errorMessage: String?) : GetCharacterRepositoryResponse()
}