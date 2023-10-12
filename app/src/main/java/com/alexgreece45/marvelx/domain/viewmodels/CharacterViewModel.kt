package com.alexgreece45.marvelx.domain.viewmodels

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.data.domains.CharacterDomain
import com.alexgreece45.marvelx.data.domains.ComicDomain
import com.alexgreece45.marvelx.data.models.character.comic.toDomain
import com.alexgreece45.marvelx.domain.database.FavoriteCharacterEntity
import com.alexgreece45.marvelx.domain.interactors.CharacterInteractor
import com.alexgreece45.marvelx.domain.interactors.CharacterPartialState
import com.alexgreece45.marvelx.domain.interactors.GetCharacterComicsPartialState
import com.alexgreece45.marvelx.domain.interactors.IsCharacterFavoritePartialState
import com.alexgreece45.marvelx.ui.base.BaseEffect
import com.alexgreece45.marvelx.ui.base.BaseEvent
import com.alexgreece45.marvelx.ui.base.BaseState
import com.alexgreece45.marvelx.ui.base.BaseViewModel
import com.alexgreece45.marvelx.util.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CharacterEvent : BaseEvent {
    data class InitView(val characterId: Int) : CharacterEvent()
    object OnBackClick : CharacterEvent()

    object OnFavoriteClick : CharacterEvent()

    data class OnComicClick(val comicId: Int) : CharacterEvent()

    object OnArrowRightClick : CharacterEvent()
}

sealed class CharacterEffect : BaseEffect {
    object OnBackClick : CharacterEffect()
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
    val errorMessage: String? = null,

    // Data.
    val character: CharacterDomain? = null,
    val comics: List<ComicDomain> = listOf()
) : BaseState {
    companion object {
        fun getDefaultValues(): CharacterState =
            CharacterState(
                isLoading = true,
                hasInternetConnection = true,
                errorMessage = null,
                character = null,
                comics = listOf()
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

            is CharacterEvent.OnBackClick -> {
                setEffect {
                    CharacterEffect.OnBackClick
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
            interactor.getCharacter(characterId).collect { characterPartialState ->
                when (characterPartialState) {
                    is CharacterPartialState.Success -> {
                        // Search if character is favorite.
                        interactor.isCharacterByIdFavorite(characterId = characterId)
                            .collect { isCharacterFavoritePartialState ->
                                when (isCharacterFavoritePartialState) {
                                    is IsCharacterFavoritePartialState.IsNotFavorite -> {
                                        setState {
                                            copy(
                                                isLoading = false,
                                                character = characterPartialState.data.copy(
                                                    isFavorite = false
                                                )
                                            )
                                        }

                                        // Load Comics.
                                        loadComics(characterId = characterId)
                                    }

                                    is IsCharacterFavoritePartialState.IsFavorite -> {
                                        setState {
                                            copy(
                                                isLoading = false,
                                                character = characterPartialState.data.copy(
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

                    is CharacterPartialState.Error -> {
                        setState {
                            copy(
                                isLoading = false,
                                errorMessage = characterPartialState.errorMessage
                            )
                        }
                    }

                    is CharacterPartialState.NoInternetConnection -> {
                        setState {
                            copy(
                                isLoading = false,
                                hasInternetConnection = false
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
                    is GetCharacterComicsPartialState.Error -> {
                        setState {
                            copy(
                                isLoading = false,
                                errorMessage = it.errorMessage
                            )
                        }
                    }

                    is GetCharacterComicsPartialState.NoInternetConnection -> {
                        setState {
                            copy(
                                isLoading = false,
                                hasInternetConnection = false
                            )
                        }
                    }

                    is GetCharacterComicsPartialState.Success -> {
                        setState {
                            copy(
                                isLoading = false,
                                comics = it.comics.toDomain()
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
                            .collect { characterPartialState ->
                                when (characterPartialState) {
                                    is CharacterPartialState.Error -> {
                                        setState {
                                            copy(
                                                errorMessage = characterPartialState.errorMessage
                                            )
                                        }
                                    }

                                    is CharacterPartialState.NoInternetConnection -> {
                                        setState {
                                            copy(
                                                hasInternetConnection = false
                                            )
                                        }
                                    }

                                    is CharacterPartialState.Success -> {
                                        setState {
                                            copy(
                                                character = characterDomain.copy(isFavorite = false)
                                            )
                                        }

                                        setEffect {
                                            CharacterEffect.DisplayOnFavoriteClickMessage(
                                                message = resourceProvider.getString(R.string.not_favorite_message, "character")
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
                        interactor.insertFavoriteCharacters(
                            FavoriteCharacterEntity(characterId = characterDomain.id)
                        ).collect { characterPartialState ->
                            when (characterPartialState) {
                                is CharacterPartialState.Error -> {
                                    setState {
                                        copy(
                                            errorMessage = characterPartialState.errorMessage
                                        )
                                    }
                                }

                                is CharacterPartialState.NoInternetConnection -> {
                                    setState {
                                        copy(
                                            hasInternetConnection = false
                                        )
                                    }
                                }

                                is CharacterPartialState.Success -> {
                                    setState {
                                        copy(
                                            character = characterDomain.copy(isFavorite = true)
                                        )
                                    }

                                    setEffect {
                                        CharacterEffect.DisplayOnFavoriteClickMessage(
                                            message = resourceProvider.getString(R.string.favorite_message, "character")
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