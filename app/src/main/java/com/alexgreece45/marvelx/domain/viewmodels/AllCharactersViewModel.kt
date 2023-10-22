package com.alexgreece45.marvelx.domain.viewmodels

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.data.domains.character.CharacterDomain
import com.alexgreece45.marvelx.domain.database.FavoriteCharacterEntity
import com.alexgreece45.marvelx.domain.interactors.CharacterInteractor
import com.alexgreece45.marvelx.domain.interactors.CharacterPartialState
import com.alexgreece45.marvelx.domain.interactors.GetFavoriteCharactersPartialState
import com.alexgreece45.marvelx.ui.BottomBarAction
import com.alexgreece45.marvelx.ui.SortingType
import com.alexgreece45.marvelx.ui.base.BaseEffect
import com.alexgreece45.marvelx.ui.base.BaseEvent
import com.alexgreece45.marvelx.ui.base.BaseState
import com.alexgreece45.marvelx.ui.base.BaseViewModel
import com.alexgreece45.marvelx.util.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AllCharactersScreenErrorView(
    val errorMessage: String,
    val onRetryClick: () -> Unit
)

sealed interface AllCharactersEvent : BaseEvent {
    object InitView : AllCharactersEvent
    object LoadAllCharacters : AllCharactersEvent
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
    sealed interface Navigation : AllCharactersEffect {
        data class NavigateToCharacterScreen(val characterId: Int) : Navigation
    }

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

    object CloseKeyboard : AllCharactersEffect
}

data class AllCharactersState(
    // Base states.
    val isLoading: Boolean = false,
    val error: AllCharactersScreenErrorView? = null,

    // Data.
    val characters: List<CharacterDomain> = emptyList(),
    val sortingType: SortingType = SortingType.getDefaultSortingType(),

    /**
     * The search bar's term.
     * */
    val searchBarTerm: String = "",

    /**
     * The filtered characters list that is shown; if there is no search term, then all the characters are shown.
     * */
    val filteredCharacters: List<CharacterDomain> = emptyList(),

    /**
     * This field tracks if the fragment of this ViewModel has been launched for the first time.
     * The value is set to true by default, and only changes, to false, when navigation occurs to the [CharacterScreen].
     * */
    val isFirstLaunch: Boolean = true
) : BaseState {
    companion object {
        fun getDefaultValues(): AllCharactersState =
            AllCharactersState(
                isLoading = false,
                error = null,
                characters = emptyList(),
                sortingType = SortingType.getDefaultSortingType(),
                searchBarTerm = "",
                filteredCharacters = emptyList(),
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

    override fun handleEvents(event: AllCharactersEvent) {
        when (event) {
            AllCharactersEvent.InitView -> {
                initView()
            }

            AllCharactersEvent.LoadAllCharacters -> {
                loadAllCharacters()
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
                    AllCharactersEffect.Navigation.NavigateToCharacterScreen(
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
                        val currentFilteredCharacters =
                            currentState.characters.map { allCharactersCharacter ->
                                currentState.filteredCharacters.firstOrNull { filteredCharactersCharacter ->
                                    filteredCharactersCharacter.id == allCharactersCharacter.id
                                } ?: allCharactersCharacter
                            }
                        copy(
                            filteredCharacters = currentFilteredCharacters.filter { character ->
                                character.name.contains(event.searchTerm, ignoreCase = true)
                            }
                        )
                    }
                }
            }

            AllCharactersEvent.SearchBar.OnCloseClick -> {
                setState {
                    copy(
                        searchBarTerm = "",
                        filteredCharacters = currentState.characters.map { allCharactersCharacter ->
                            currentState.filteredCharacters.firstOrNull { filteredCharactersCharacter ->
                                filteredCharactersCharacter.id == allCharactersCharacter.id
                            } ?: allCharactersCharacter
                        }
                    )
                }

                setEffect {
                    AllCharactersEffect.CloseKeyboard
                }
            }
        }
    }


    private fun initView() {
        setState {
            AllCharactersState.getDefaultValues()
        }
        loadAllCharacters()
    }

    private fun loadAllCharacters() {
        setState {
            copy(
                isLoading = true,
                error = null
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
                                        // Move favorite characters data from local storage to the characters list.
                                        setState {
                                            val characters = it.data.map { character ->
                                                character.copy(
                                                    isFavorite = favoriteCharacterPartialState.favoriteCharacters.firstOrNull { favoriteCharacterEntity ->
                                                        favoriteCharacterEntity.characterId == character.id
                                                    }?.let {
                                                        true
                                                    } ?: false
                                                )
                                            }
                                            copy(
                                                isLoading = false,
                                                characters = characters,
                                                filteredCharacters = characters // Sets the initial filteredCharacters' value.
                                            )
                                        }
                                    }

                                    is GetFavoriteCharactersPartialState.Error -> {
                                        // Could not retrieve favorite characters data.
                                        setState {
                                            copy(
                                                isLoading = false,
                                                error = AllCharactersScreenErrorView(
                                                    errorMessage = favoriteCharacterPartialState.errorMessage,
                                                    onRetryClick = { loadAllCharacters() }
                                                )
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
                                error = AllCharactersScreenErrorView(
                                    errorMessage = it.errorMessage,
                                    onRetryClick = { loadAllCharacters() }
                                )
                            )
                        }
                    }

                    is CharacterPartialState.NoInternetConnection -> {
                        setState {
                            copy(
                                isLoading = false,
                                error = AllCharactersScreenErrorView(
                                    errorMessage = it.errorMessage,
                                    onRetryClick = { loadAllCharacters() }
                                )
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
                                    filteredCharacters = currentState.filteredCharacters.map { character ->
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
                                    isLoading = false,
                                    error = AllCharactersScreenErrorView(
                                        errorMessage = favoriteCharacterPartialState.errorMessage,
                                        onRetryClick = { syncFavoriteCharacters() }
                                    )
                                )
                            }
                        }
                    }
                }
        }
    }

    private fun onHeartClick(characterId: Int) {
        // Retrieve character.
        val character = currentState.filteredCharacters.firstOrNull { it.id == characterId }
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
            setState {
                copy(
                    error = AllCharactersScreenErrorView(
                        errorMessage = resourceProvider.getGenericErrorMessage(),
                        onRetryClick = { onHeartClick(characterId) }
                    )
                )
            }
        }
    }

    private fun unFavoriteCharacterById(characterId: Int) {
        viewModelScope.launch {
            interactor.deleteFavoriteCharacterById(characterId = characterId).collect {
                when (it) {
                    is CharacterPartialState.Success -> {
                        setState {
                            copy(
                                filteredCharacters = currentState.filteredCharacters.map { character ->
                                    if (character.id == characterId) character.copy(
                                        isFavorite = false
                                    ) else character
                                }
                            )
                        }
                        setEffect {
                            AllCharactersEffect.UnFavoriteCharacter.Success(
                                successMessage = resourceProvider.getString(
                                    R.string.not_favorite_message,
                                    resourceProvider.getString(R.string.character)
                                )
                            )
                        }
                    }

                    is CharacterPartialState.Error -> {
                        setState {
                            copy(
                                error = AllCharactersScreenErrorView(
                                    errorMessage = it.errorMessage,
                                    onRetryClick = { unFavoriteCharacterById(characterId) }
                                )
                            )
                        }
                    }

                    is CharacterPartialState.NoInternetConnection -> {
                        setState {
                            copy(
                                error = AllCharactersScreenErrorView(
                                    errorMessage = it.errorMessage,
                                    onRetryClick = { unFavoriteCharacterById(characterId) }
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun favoriteCharacterById(characterId: Int) {
        viewModelScope.launch {
            interactor.insertFavoriteCharacters(
                FavoriteCharacterEntity(characterId = characterId)
            ).collect {
                when (it) {
                    is CharacterPartialState.Success -> {
                        setState {
                            copy(
                                filteredCharacters = currentState.filteredCharacters.map { character ->
                                    if (character.id == characterId) character.copy(
                                        isFavorite = true
                                    ) else character
                                }
                            )
                        }
                        setEffect {
                            AllCharactersEffect.UnFavoriteCharacter.Success(
                                successMessage = resourceProvider.getString(
                                    R.string.favorite_message,
                                    resourceProvider.getString(R.string.character)
                                )
                            )
                        }
                    }

                    is CharacterPartialState.Error -> {
                        setState {
                            copy(
                                error = AllCharactersScreenErrorView(
                                    errorMessage = it.errorMessage,
                                    onRetryClick = { favoriteCharacterById(characterId) }
                                )
                            )
                        }
                    }

                    is CharacterPartialState.NoInternetConnection -> {
                        setState {
                            copy(
                                error = AllCharactersScreenErrorView(
                                    errorMessage = it.errorMessage,
                                    onRetryClick = { favoriteCharacterById(characterId) }
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}