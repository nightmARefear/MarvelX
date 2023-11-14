package com.alexgreece45.marvelx.viewModels

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.data.domains.character.CharacterDomain
import com.alexgreece45.marvelx.data.domains.character.toFavoriteCharacterEntity
import com.alexgreece45.marvelx.data.domains.comic.ComicDomain
import com.alexgreece45.marvelx.interactors.CharacterInteractor
import com.alexgreece45.marvelx.interactors.DeleteFavoriteCharacterByIdPartialState
import com.alexgreece45.marvelx.interactors.GetCharacterComicsPartialState
import com.alexgreece45.marvelx.interactors.GetCharacterPartialState
import com.alexgreece45.marvelx.interactors.InsertFavoriteCharactersPartialState
import com.alexgreece45.marvelx.interactors.IsCharacterFavoriteByIdPartialState
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.util.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CharacterScreenErrorView(
    val errorMessage: String,
    val onRetryClick: () -> Unit
)

sealed class CharacterEvent : BaseEvent {
    data class InitView(val characterId: Int) : CharacterEvent()

    data class LoadCharacter(val characterId: Int) : CharacterEvent()

    object OnBackClick : CharacterEvent()

    object OnFavoriteClick : CharacterEvent()

    data class OnComicClick(val comicId: Int) : CharacterEvent()

    object OnArrowRightClick : CharacterEvent()
}

sealed class CharacterEffect : BaseEffect {
    object PopBackStack : CharacterEffect()
    data class NavigateToComicScreen(val comicId: Int) : CharacterEffect()

    data class NavigateToAllComicsScreen(val characterId: Int, val toolbarTitlePrefix: String) :
        CharacterEffect()

    data class DisplayError(val errorMessage: String) : CharacterEffect()

    data class DisplayOnFavoriteClickMessage(val message: String) : CharacterEffect()
}

data class CharacterState(
    // Base states.
    val isLoading: Boolean = true,
    val hasInternetConnection: Boolean = true,
    val error: CharacterScreenErrorView? = null,

    // Data.
    val character: CharacterDomain? = null,
    val comics: List<ComicDomain> = emptyList()
) : BaseState {
    companion object {
        fun getDefaultValues(): CharacterState =
            CharacterState(
                isLoading = true,
                hasInternetConnection = true,
                error = null,
                character = null,
                comics = emptyList()
            )
    }
}

@HiltViewModel
class CharacterViewModel @Inject constructor(
    private val interactor: CharacterInteractor,
    private val resourceProvider: ResourceProvider
) : BaseViewModel<CharacterEvent, CharacterState, CharacterEffect>() {
    override fun setInitialState(): CharacterState = CharacterState()

    override fun handleEvents(event: CharacterEvent) {
        when (event) {
            is CharacterEvent.InitView -> {
                initView(characterId = event.characterId)
            }

            is CharacterEvent.LoadCharacter -> {
                setState {
                    copy(
                        hasInternetConnection = true,
                        error = null
                    )
                }
                loadCharacter(event.characterId)
            }

            is CharacterEvent.OnBackClick -> {
                setEffect {
                    CharacterEffect.PopBackStack
                }
            }

            is CharacterEvent.OnComicClick -> {
                setEffect {
                    CharacterEffect.NavigateToComicScreen(comicId = event.comicId)
                }
            }

            is CharacterEvent.OnFavoriteClick -> {
                onFavoriteClick()
            }

            CharacterEvent.OnArrowRightClick -> {
                currentState.character?.let { characterDomain ->
                    setEffect {
                        CharacterEffect.NavigateToAllComicsScreen(
                            characterId = characterDomain.id,
                            toolbarTitlePrefix = if (characterDomain.name.endsWith("s")) characterDomain.name + "'" else characterDomain.name + "'s"
                        )
                    }
                } ?: run {
                    // Character is null, so display a Toast with the related error message. todo: display as snackbar
                    setEffect {
                        CharacterEffect.DisplayError(
                            errorMessage = resourceProvider.getString(R.string.character_screen_error_loading_all_comics)
                        )
                    }
                }
            }
        }
    }

    private fun initView(characterId: Int) {
        setState {
            CharacterState.getDefaultValues()
        }
        loadCharacter(characterId = characterId)
    }

    private fun loadCharacter(characterId: Int) {
        setState {
            copy(
                isLoading = true
            )
        }
        viewModelScope.launch {
            interactor.getCharacter(characterId).collect { getCharacterPartialState ->
                when (getCharacterPartialState) {
                    is GetCharacterPartialState.Success -> {
                        // Search if character is favorite.
                        interactor.isCharacterFavoriteById(characterId = characterId)
                            .collect { isCharacterFavoritePartialState ->
                                when (isCharacterFavoritePartialState) {
                                    is IsCharacterFavoriteByIdPartialState.IsNotFavorite -> {
                                        setState {
                                            copy(
                                                character = getCharacterPartialState.character.copy(
                                                    isFavorite = false
                                                )
                                            )
                                        }

                                        // Load Comics.
                                        loadComics(characterId = characterId)
                                    }

                                    is IsCharacterFavoriteByIdPartialState.IsFavorite -> {
                                        setState {
                                            copy(
                                                character = getCharacterPartialState.character.copy(
                                                    isFavorite = true
                                                )
                                            )
                                        }

                                        // Load Comics.
                                        loadComics(characterId = characterId)
                                    }
                                }
                            }
                    }

                    is GetCharacterPartialState.Failed -> {
                        setState {
                            copy(
                                isLoading = false,
                                error = CharacterScreenErrorView(
                                    errorMessage = getCharacterPartialState.errorMessage,
                                    onRetryClick = { loadCharacter(characterId) }
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun loadComics(characterId: Int) {
        viewModelScope.launch {
            interactor.getCharacterComics(characterId).collect {
                when (it) {
                    is GetCharacterComicsPartialState.Failed -> {
                        setState {
                            copy(
                                isLoading = false,
                                error = CharacterScreenErrorView(
                                    errorMessage = it.errorMessage,
                                    onRetryClick = { loadComics(characterId) }
                                )
                            )
                        }
                    }

                    is GetCharacterComicsPartialState.NoInternetConnection -> {
                        setState {
                            copy(
                                isLoading = false,
                                error = CharacterScreenErrorView(
                                    errorMessage = resourceProvider.getNoInternetConnectionMessage(),
                                    onRetryClick = { loadComics(characterId) }
                                )
                            )
                        }
                    }

                    is GetCharacterComicsPartialState.Success -> {
                        setState {
                            copy(
                                isLoading = false,
                                comics = it.comics
                            )
                        }
                    }
                }
            }
        }
    }

    private fun onFavoriteClick() {
        currentState.character?.let { characterDomain ->
            when (characterDomain.isFavorite) {
                // Character is favorite; proceed to remove it from favorites.
                true -> {
                    viewModelScope.launch {
                        interactor.deleteFavoriteCharacterById(characterId = characterDomain.id)
                            .collect {
                                when (it) {
                                    is DeleteFavoriteCharacterByIdPartialState.Failed -> {
                                        setState {
                                            copy(
                                                error = CharacterScreenErrorView(
                                                    errorMessage = it.errorMessage,
                                                    onRetryClick = { onFavoriteClick() }
                                                )
                                            )
                                        }
                                    }

                                    is DeleteFavoriteCharacterByIdPartialState.Success -> {
                                        setState {
                                            copy(
                                                character = characterDomain.copy(isFavorite = false)
                                            )
                                        }

                                        setEffect {
                                            CharacterEffect.DisplayOnFavoriteClickMessage(
                                                message = resourceProvider.getString(
                                                    R.string.not_favorite_message,
                                                    "character"
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                    }
                }

                // Character is not favorite; proceed to make it favorite.
                false -> {
                    viewModelScope.launch {
                        interactor.insertFavoriteCharacters(characterDomain.toFavoriteCharacterEntity())
                            .collect { insertFavoriteCharactersPartialState ->
                                when (insertFavoriteCharactersPartialState) {
                                    is InsertFavoriteCharactersPartialState.Failed -> {
                                        setState {
                                            copy(
                                                error = CharacterScreenErrorView(
                                                    errorMessage = insertFavoriteCharactersPartialState.errorMessage,
                                                    onRetryClick = { onFavoriteClick() }
                                                )
                                            )
                                        }
                                    }

                                    is InsertFavoriteCharactersPartialState.Success -> {
                                        setState {
                                            copy(
                                                character = characterDomain.copy(isFavorite = true)
                                            )
                                        }

                                        setEffect {
                                            CharacterEffect.DisplayOnFavoriteClickMessage(
                                                message = resourceProvider.getString(
                                                    R.string.favorite_message,
                                                    "character"
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                    }
                }
            }
        } ?: run {
            // show snackbar with error
            TODO("To be implemented in the future....")
        }
    }
}