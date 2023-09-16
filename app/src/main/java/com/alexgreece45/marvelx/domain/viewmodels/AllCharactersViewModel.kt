package com.alexgreece45.marvelx.domain.viewmodels

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.data.domains.CharacterDomain
import com.alexgreece45.marvelx.domain.database.FavoriteCharacterEntity
import com.alexgreece45.marvelx.domain.interactors.CharacterInteractor
import com.alexgreece45.marvelx.domain.interactors.CharacterPartialState
import com.alexgreece45.marvelx.domain.interactors.GetFavoriteCharactersPartialState
import com.alexgreece45.marvelx.ui.base.BaseEffect
import com.alexgreece45.marvelx.ui.base.BaseEvent
import com.alexgreece45.marvelx.ui.base.BaseState
import com.alexgreece45.marvelx.ui.base.BaseViewModel
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.SortingType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AllCharactersEvent : BaseEvent {
    object InitView : AllCharactersEvent
    object LoadFavoriteCharacters : AllCharactersEvent
    data class OnCharacterClick(val characterId: Int) : AllCharactersEvent
    data class OnHeartClick(val characterId: Int) : AllCharactersEvent

    object OnFabClick : AllCharactersEvent

    sealed class Sorting : AllCharactersEvent {
        data class OnItemClick(val sortingType: SortingType) : Sorting()
        object OnCloseClick : Sorting()
    }

    sealed class SearchBar : AllCharactersEvent {
        data class OnKeyboardType(val searchTerm: String) : SearchBar()

        object OnCloseClick : SearchBar()
    }
}

sealed interface AllCharactersEffect : BaseEffect {
    data class NavigateToCharacterScreen(val characterId: Int) : AllCharactersEffect
    sealed interface FavoriteCharacter : AllCharactersEffect {
        data class Success(val successMessage: String) : FavoriteCharacter
        data class Error(val errorMessage: String) : FavoriteCharacter
    }

    sealed interface UnFavoriteCharacter : AllCharactersEffect {
        data class Success(val successMessage: String) : UnFavoriteCharacter
        data class Error(val errorMessage: String) : UnFavoriteCharacter
    }

    sealed interface SortingBottomSheet : AllCharactersEffect {
        object Show : SortingBottomSheet
        object Hide : SortingBottomSheet
    }

    object CloseKeyboard: AllCharactersEffect
}

data class AllCharactersState(
    // Base states.
    val isLoading: Boolean = true,
    val hasInternetConnection: Boolean = true,
    val errorMessage: String? = null,

    // Data.
    val characters: List<CharacterDomain> = mutableListOf(),
    val sortingType: SortingType = SortingType.getDefaultSortingType(),
    /**
     * The search bar's term.
     * */
    val searchBarTerm: String = "",
    /**
     * The filtered characters list that is shown; if there is no search term, then all the characters are shown.
     * */
    val filteredCharacters: List<CharacterDomain> = mutableListOf(),

    /**
     * This field tracks if the fragment of this ViewModel has been launched for the first time.
     * The value is set to true by default, and only changes, to false, when navigation occurs to the [CharacterScreen].
     * */
    val isFirstLaunch: Boolean = true
) : BaseState {
    companion object {
        fun getDefaultValues(): AllCharactersState =
            AllCharactersState(
                isLoading = true,
                hasInternetConnection = true,
                errorMessage = null,
                characters = mutableListOf(),
                isFirstLaunch = true
            )
    }
}

@HiltViewModel
class AllCharactersViewModel @Inject constructor(
    private val interactor: CharacterInteractor,
    private val resourceProvider: ResourceProvider
) : BaseViewModel<AllCharactersEvent, AllCharactersState, AllCharactersEffect>() {
    override fun setInitialState(): AllCharactersState = AllCharactersState.getDefaultValues()

    init {
        setEvent(AllCharactersEvent.InitView)
    }

    override suspend fun handleEvents(event: AllCharactersEvent) {
        when (event) {
            AllCharactersEvent.InitView -> {
                initView()
            }

            is AllCharactersEvent.OnCharacterClick -> {
                setState {
                    copy(
                        isFirstLaunch = false
                    )
                }
                setEffect {
                    AllCharactersEffect.CloseKeyboard
                }
                setEffect {
                    AllCharactersEffect.NavigateToCharacterScreen(
                        characterId = event.characterId
                    )
                }
            }

            is AllCharactersEvent.OnHeartClick -> {
                onHeartClick(characterId = event.characterId)
            }

            is AllCharactersEvent.LoadFavoriteCharacters -> {
                syncFavoriteCharacters()
            }

            AllCharactersEvent.OnFabClick -> {
                setEffect {
                    AllCharactersEffect.SortingBottomSheet.Show
                }
            }

            AllCharactersEvent.Sorting.OnCloseClick -> {
                setEffect {
                    AllCharactersEffect.SortingBottomSheet.Hide
                }
            }

            is AllCharactersEvent.Sorting.OnItemClick -> {
                setEffect {
                    AllCharactersEffect.SortingBottomSheet.Hide
                }
                // Selected sorting type is the same as the old one, so don't perform further actions.
                if (event.sortingType == currentState.sortingType) return

                when (event.sortingType) {
                    SortingType.Random -> {
                        setState {
                            copy(
                                filteredCharacters = currentState.filteredCharacters.shuffled(),
                                sortingType = event.sortingType
                            )
                        }
                    }

                    SortingType.TextAscending -> {
                        setState {
                            copy(
                                filteredCharacters = currentState.filteredCharacters.sortedBy { it.name },
                                sortingType = event.sortingType
                            )
                        }
                    }

                    SortingType.TextDescending -> {
                        setState {
                            copy(
                                filteredCharacters = currentState.filteredCharacters.sortedByDescending { it.name },
                                sortingType = event.sortingType
                            )
                        }
                    }
                }
            }

            is AllCharactersEvent.SearchBar.OnKeyboardType -> {
                if (event.searchTerm != currentState.searchBarTerm) {
                    setState {
                        copy(
                            searchBarTerm = event.searchTerm
                        )
                    }

                    setState {
                        copy(
                            filteredCharacters = currentState.characters.filter { it.name.lowercase().contains(event.searchTerm.lowercase()) }
                        )
                    }
                }
            }

            AllCharactersEvent.SearchBar.OnCloseClick -> {
                setState {
                    copy(
                        searchBarTerm = "",
                        filteredCharacters = currentState.characters
                    )
                }

                setEffect {
                    AllCharactersEffect.CloseKeyboard
                }
            }
        }
    }

    private suspend fun initView() {
        setState {
            copy(
                isLoading = true,
                hasInternetConnection = true
            )
        }

        viewModelScope.launch {
            // Load all characters.
            interactor.getAllCharacters().collect {
                when (it) {
                    is CharacterPartialState.Success -> {
                        // Load favorite characters.
                        interactor.getFavoriteCharacters()
                            .collect { favoriteCharacterPartialState ->
                                when (favoriteCharacterPartialState) {
                                    is GetFavoriteCharactersPartialState.Success -> {
                                        // Move favorite characters data from local storage to the CharacterDomain list.
                                        setState {
                                            copy(
                                                isLoading = false,
                                                characters = it.data.map { character ->
                                                    character.copy(
                                                        isFavorite = favoriteCharacterPartialState.favoriteCharacters.firstOrNull { favoriteCharacterEntity ->
                                                            favoriteCharacterEntity.characterId == character.id
                                                        }?.let {
                                                            true
                                                        } ?: false
                                                    )
                                                }
                                            )
                                        }

                                        // Sets the initial filteredCharacters' value.
                                        setState {
                                            copy(
                                                filteredCharacters = currentState.characters
                                            )
                                        }
                                    }

                                    is GetFavoriteCharactersPartialState.Error -> {
                                        // Could not retrieve favorite characters data.
                                        setState {
                                            copy(
                                                isLoading = false,
                                                characters = it.data
                                            )
                                        }
                                    }
                                }
                            }
                    }

                    is CharacterPartialState.Error -> {
                        setState {
                            copy(
                                isLoading = false,
                                errorMessage = it.errorMessage
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

    /**
     * Syncs the favorite characters from the local database.
     * */
    private fun syncFavoriteCharacters() {
        setState {
            copy(
                isLoading = true
            )
        }

        viewModelScope.launch {
            interactor.getFavoriteCharacters()
                .collect { favoriteCharacterPartialState ->
                    when (favoriteCharacterPartialState) {
                        is GetFavoriteCharactersPartialState.Success -> {
                            // Move favorite characters data from local storage to the CharacterDomain list.
                            setState {
                                copy(
                                    isLoading = false,
                                    characters = currentState.characters.map { character ->
                                        character.copy(
                                            isFavorite = favoriteCharacterPartialState.favoriteCharacters.firstOrNull { favoriteCharacterEntity ->
                                                favoriteCharacterEntity.characterId == character.id
                                            }?.let {
                                                true
                                            } ?: false
                                        )
                                    }
                                )
                            }
                        }

                        is GetFavoriteCharactersPartialState.Error -> {
                            // Could not retrieve favorite characters data.
                            setState {
                                copy(
                                    isLoading = false
                                )
                            }
                        }
                    }
                }
        }
    }

    private suspend fun onHeartClick(characterId: Int) {
        // Retrieve character.
        val character = currentState.characters.firstOrNull { it.id == characterId }
        character?.let {
            // Check if character is favorite.
            when (character.isFavorite) {
                true -> {
                    // Character is favorite, so unfavorite it.
                    unFavoriteCharacterById(characterId = characterId)
                }

                false -> {
                    // Character is not favorite, so make it.
                    favoriteCharacterById(characterId = characterId)
                }
            }
        } ?: run {
            setEffect {
                AllCharactersEffect.FavoriteCharacter.Error(
                    errorMessage = resourceProvider.getString(R.string.unknown_error)
                )
            }
        }
    }

    private suspend fun unFavoriteCharacterById(characterId: Int) {
        viewModelScope.launch {
            interactor.deleteFavoriteCharacterById(characterId = characterId).collect {
                when (it) {
                    is CharacterPartialState.Success -> {
                        setState {
                            copy(
                                characters = currentState.characters.map { characterDomain ->
                                    if (characterDomain.id == characterId) characterDomain.copy(
                                        isFavorite = false
                                    ) else characterDomain
                                }
                            )
                        }
                        setEffect {
                            AllCharactersEffect.UnFavoriteCharacter.Success(
                                successMessage = resourceProvider.getString(
                                    R.string.not_favorite_message,
                                    "character"
                                )
                            )
                        }
                    }

                    is CharacterPartialState.Error -> {
                        setEffect {
                            AllCharactersEffect.UnFavoriteCharacter.Error(
                                errorMessage = it.errorMessage
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
                }
            }
        }
    }

    private suspend fun favoriteCharacterById(characterId: Int) {
        viewModelScope.launch {
            interactor.insertFavoriteCharacters(
                FavoriteCharacterEntity(characterId = characterId)
            ).collect {
                when (it) {
                    is CharacterPartialState.Success -> {
                        setState {
                            copy(
                                characters = currentState.characters.map { characterDomain ->
                                    if (characterDomain.id == characterId) characterDomain.copy(
                                        isFavorite = true
                                    ) else characterDomain
                                }
                            )
                        }
                        setEffect {
                            AllCharactersEffect.UnFavoriteCharacter.Success(
                                successMessage = resourceProvider.getString(
                                    R.string.favorite_message,
                                    "character"
                                )
                            )
                        }
                    }

                    is CharacterPartialState.Error -> setEffect {
                        AllCharactersEffect.FavoriteCharacter.Error(errorMessage = it.errorMessage)
                    }

                    is CharacterPartialState.NoInternetConnection -> {
                        setState {
                            copy(
                                hasInternetConnection = false
                            )
                        }
                    }
                }
            }
        }
    }
}