package com.alexgreece45.marvelx.viewModels

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.data.domains.character.toCharacterCardUiList
import com.alexgreece45.marvelx.database.FavoriteCharacterEntity
import com.alexgreece45.marvelx.interactors.CharacterInteractor
import com.alexgreece45.marvelx.interactors.DeleteFavoriteCharacterByIdPartialState
import com.alexgreece45.marvelx.interactors.GetAllCharactersPartialState
import com.alexgreece45.marvelx.interactors.GetFavoriteCharacterIdsPartialState
import com.alexgreece45.marvelx.interactors.GetFavoriteCharactersPartialState
import com.alexgreece45.marvelx.interactors.InsertFavoriteCharactersPartialState
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.components.BottomBarItem
import com.alexgreece45.marvelx.ui.components.IconData
import com.alexgreece45.marvelx.ui.components.SortingType
import com.alexgreece45.marvelx.ui.navigation.Screen
import com.alexgreece45.marvelx.ui.screens.allCharactersScreen.uiModels.CharacterCardUi
import com.alexgreece45.marvelx.ui.screens.allCharactersScreen.uiModels.toFavoriteCharacterEntity
import com.alexgreece45.marvelx.util.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AllCharactersScreenErrorView(
    val errorMessage: String,
    val onRetryClick: () -> Unit
)

sealed interface AllCharactersEvent : BaseEvent {
    data object Init : AllCharactersEvent
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

    data object CloseKeyboard : AllCharactersEffect
}

data class AllCharactersState(
    // Base states.
    val isLoading: Boolean = false,
    val error: AllCharactersScreenErrorView? = null,

    // Data.
    val bottomNavigationItems: List<BottomBarItem> = emptyList(),
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

@HiltViewModel
class AllCharactersViewModel @Inject constructor(
    private val interactor: CharacterInteractor,
    private val resourceProvider: ResourceProvider
) : BaseViewModel<AllCharactersEvent, AllCharactersState, AllCharactersEffect>() {
    override fun setInitialState(): AllCharactersState =
        AllCharactersState(
            isLoading = true,
            error = null,
            bottomNavigationItems = getBottomNavigationItems(),
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
        /*setState {
            AllCharactersState.getDefaultValues()
        }*/
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

    private fun getBottomNavigationItems(): List<BottomBarItem> = listOf(
        BottomBarItem(
            route = Screen.BottomNavigationScreens.AllCharactersScreen.route,
            label = R.string.bottom_navigation_label_characters,
            iconData = IconData(
                iconResource = R.drawable.ic_superhero,
                contentDescription = R.string.bottom_navigation_label_characters
            )
        ),
        BottomBarItem(
            route = Screen.BottomNavigationScreens.AllFavoriteCharactersScreen.route,
            label = R.string.bottom_navigation_label_favorites,
            iconData = IconData(
                iconResource = R.drawable.ic_favorite_filled,
                contentDescription = R.string.bottom_navigation_label_favorites
            )
        ),
        BottomBarItem(
            route = Screen.BottomNavigationScreens.ProfileScreen.route,
            label = R.string.bottom_navigation_label_profile,
            iconData = IconData(
                iconResource = R.drawable.ic_person,
                contentDescription = R.string.bottom_navigation_label_profile
            )
        )
    )
}