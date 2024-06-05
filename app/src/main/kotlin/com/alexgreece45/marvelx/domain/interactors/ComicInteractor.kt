package com.alexgreece45.marvelx.domain.interactors

import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.controllers.AppLoggerController
import com.alexgreece45.marvelx.network_logic.api.ApiClient
import com.alexgreece45.marvelx.domain.model_api.comic.ComicDomain
import com.alexgreece45.marvelx.network_logic.models_api.character.comic.toComicDomain
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.extensions.safeAsync
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException

interface ComicInteractor {
    fun getComicById(comicId: Int): Flow<GetComicPartialState>
}

class ComicInteractorImpl(
    private val api: ApiClient,
    private val resourceProvider: ResourceProvider,
    private val logger: AppLoggerController
) : ComicInteractor {
    override fun getComicById(comicId: Int): Flow<GetComicPartialState> = flow {
        val response = api.getComicById(comicId)

        when (response.isSuccessful) {
            true -> {
                response.body()?.let { comicDataWrapper ->
                    if (comicDataWrapper.data.results.isEmpty()) {
                        emit(
                            GetComicPartialState.Error(
                                errorMessage = resourceProvider.getString(R.string.comic_screen_comic_could_not_be_retrieved)
                            )
                        )
                    } else {
                        logger.d { comicDataWrapper.data.results[0].toComicDomain().toString() }
                        emit(
                            GetComicPartialState.Success(
                                comic = comicDataWrapper.data.results[0].toComicDomain()
                            )
                        )
                    }
                } ?: run {
                    emit(
                        GetComicPartialState.Error(
                            errorMessage = response.errorBody().toString()
                        )
                    )
                }
            }

            false -> {
                emit(
                    GetComicPartialState.Error(
                        errorMessage = response.errorBody().toString()
                    )
                )
            }
        }
    }.safeAsync {
        when (it) {
            is IOException -> {
                GetComicPartialState.NoInternetConnection
            }

            else -> {
                GetComicPartialState.Error(
                    errorMessage = it.message.toString()
                )
            }
        }
    }
}

sealed interface GetComicPartialState {
    data class Success(val comic: ComicDomain) : GetComicPartialState
    data class Error(val errorMessage: String) : GetComicPartialState
    data object NoInternetConnection : GetComicPartialState
}