package com.alexgreece45.marvelx.ui.screens.allCharacters

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.database.FavoriteCharacterEntity
import com.alexgreece45.marvelx.domain.interactors.CharacterInteractor
import com.alexgreece45.marvelx.domain.interactors.DeleteFavoriteCharacterByIdPartialState
import com.alexgreece45.marvelx.domain.interactors.GetAllCharactersPartialState
import com.alexgreece45.marvelx.domain.interactors.GetFavoriteCharacterIdsPartialState
import com.alexgreece45.marvelx.domain.interactors.GetFavoriteCharactersPartialState
import com.alexgreece45.marvelx.domain.interactors.InsertFavoriteCharactersPartialState
import com.alexgreece45.marvelx.domain.model_api.character.toCharacterCardUiList
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.components.SortingType
import com.alexgreece45.marvelx.ui.screens.allCharacters.uiModel.CharacterCardUi
import com.alexgreece45.marvelx.ui.screens.allCharacters.uiModel.toFavoriteCharacterEntity
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.ui.screens.allCharacters.model.AllCharactersScreenErrorView
import kotlinx.coroutines.launch

sealed interface AllCharactersEvent : BaseEvent {
    data object Init : AllCharactersEvent

    /**
     * Pull to refresh.
     */
    data object OnRefresh : AllCharactersEvent
    data object LoadAllCharacters : AllCharactersEvent
    data object LoadFavoriteCharacters : AllCharactersEvent
    data class OnCharacterClick(val characterId: Int) : AllCharactersEvent
    data class OnHeartClick(val characterId: Int) : AllCharactersEvent

    data object OnFabClick : AllCharactersEvent

    sealed class Sorting : AllCharactersEvent {
        data class OnItemClick(val sortingType: SortingType) : Sorting()
        data object OnCloseClick : Sorting()
    }

    sealed class SearchBar : AllCharactersEvent {
        data class OnKeyboardType(val searchTerm: String) : SearchBar()

        data object OnCloseClick : SearchBar()
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
        data object Show : SortingBottomSheet
        data object Hide : SortingBottomSheet
    }

    data object CloseKeyboardAndRemoveFocus : AllCharactersEffect
}

data class AllCharactersState(
    // Base states.
    val isLoading: Boolean = false,
    val error: AllCharactersScreenErrorView? = null,

    // Data.
    val characters: List<CharacterCardUi> = emptyList(),
    val sortingType: SortingType = SortingType.DEFAULT_SORTING_TYPE,

    /**
     * The search bar's term.
     */
    val searchBarTerm: String = "",

    /**
     * The filtered characters list that is shown; if there is no search term, then all the characters are shown.
     */
    val filteredCharacters: List<CharacterCardUi> = emptyList(),

    /**
     * This field tracks if the fragment of this ViewModel has been launched for the first time.
     * The value is set to true by default, and only changes, to false, when navigation occurs to the `CharacterScreen`.
     */
    val isFirstLaunch: Boolean = true
) : BaseState

class AllCharactersViewModel(
    private val interactor: CharacterInteractor,
    private val resourceProvider: ResourceProvider
) : BaseViewModel<AllCharactersEvent, AllCharactersState, AllCharactersEffect>() {
    override fun setInitialState(): AllCharactersState =
        AllCharactersState(
            isLoading = true,
            error = null,
            characters = emptyList(),
            sortingType = SortingType.DEFAULT_SORTING_TYPE,
            searchBarTerm = "",
            filteredCharacters = emptyList(),
            isFirstLaunch = true
        )

    override fun handleEvents(event: AllCharactersEvent) {
        when (event) {
            AllCharactersEvent.Init -> {
                initView()
            }

            AllCharactersEvent.OnRefresh -> {
                pullToRefresh()
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
                    AllCharactersEffect.CloseKeyboardAndRemoveFocus
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
                    AllCharactersEffect.CloseKeyboardAndRemoveFocus
                }
            }
        }
    }

    /**
     * This function is ONLY invoked when the screen first launches.
     */
    private fun initView() {
        loadAllCharacters()
    }

    /**
     * This function is invoked when the pull-to-refresh functionality has been triggered by the user.
     */
    private fun pullToRefresh() {
        setEffect {
            AllCharactersEffect.CloseKeyboardAndRemoveFocus
        }

        // Reset all states.
        setState {
            setInitialState()
        }

        // Initialize view.
        initView()
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
            interactor.getAllCharacters().collect { getAllCharactersPartialState ->
                when (getAllCharactersPartialState) {
                    is GetAllCharactersPartialState.Success -> {
                        // Load favorite characters.
                        interactor.getFavoriteCharacterIds()
                            .collect { favoriteCharacterPartialState ->
                                when (favoriteCharacterPartialState) {
                                    is GetFavoriteCharacterIdsPartialState.Success -> {
                                        // Move favorite characters data from local storage to the characters list.
                                        setState {
                                            val characters =
                                                getAllCharactersPartialState.characters.map { character ->
                                                    character.copy(
                                                        isFavorite = favoriteCharacterPartialState.favoriteCharacterIds.firstOrNull { favoriteCharacterId ->
                                                            favoriteCharacterId == character.id
                                                        }?.let {
                                                            true
                                                        } ?: false
                                                    )
                                                }.toCharacterCardUiList()
                                            copy(
                                                isLoading = false,
                                                characters = characters,
                                                filteredCharacters = characters // Sets the initial filteredCharacters' value.
                                            )
                                        }
                                    }

                                    is GetFavoriteCharacterIdsPartialState.Failed -> {
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

                    is GetAllCharactersPartialState.Failed -> {
                        setState {
                            copy(
                                isLoading = false,
                                error = AllCharactersScreenErrorView(
                                    errorMessage = getAllCharactersPartialState.errorMessage,
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

                        is GetFavoriteCharactersPartialState.Failed -> {
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
                    // Character is favorite, so un-favorite it.
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
                    is DeleteFavoriteCharacterByIdPartialState.Success -> {
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

                    is DeleteFavoriteCharacterByIdPartialState.Failed -> {
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
        val character: FavoriteCharacterEntity? =
            currentState.filteredCharacters.firstOrNull { it.id == characterId }
                ?.toFavoriteCharacterEntity()
        if (character == null) {
            setState {
                copy(
                    error = AllCharactersScreenErrorView(
                        errorMessage = resourceProvider.getGenericErrorMessage(), // TODO: Refactor error handling by displaying a toast.
                        onRetryClick = { favoriteCharacterById(characterId) }
                    )
                )
            }
            return
        }

        viewModelScope.launch {
            interactor.insertFavoriteCharacters(character)
                .collect { insertFavoriteCharactersPartialState ->
                    when (insertFavoriteCharactersPartialState) {
                        is InsertFavoriteCharactersPartialState.Success -> {
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

                        is InsertFavoriteCharactersPartialState.Failed -> {
                            setState {
                                copy(
                                    error = AllCharactersScreenErrorView(
                                        errorMessage = insertFavoriteCharactersPartialState.errorMessage,
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