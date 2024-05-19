package com.alexgreece45.marvelx.viewModels

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.database.toCharacterCardUiList
import com.alexgreece45.marvelx.interactors.CharacterInteractor
import com.alexgreece45.marvelx.interactors.GetFavoriteCharactersPartialState
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.components.BottomBarItem
import com.alexgreece45.marvelx.ui.components.IconData
import com.alexgreece45.marvelx.ui.navigation.Screen
import com.alexgreece45.marvelx.ui.screens.allCharactersScreen.uiModels.CharacterCardUi
import kotlinx.coroutines.launch

data class AllFavoriteCharactersScreenErrorView(
    val errorMessage: String,
    val onRetryClick: () -> Unit
)

sealed class AllFavoriteCharactersEvent : BaseEvent {
    data object Init : AllFavoriteCharactersEvent()
}

data class AllFavoriteCharactersState(
    val isLoading: Boolean = false,
    val error: AllFavoriteCharactersScreenErrorView? = null,

    // Data.
    val bottomNavigationItems: List<BottomBarItem> = emptyList(),
    val favoriteCharacters: List<CharacterCardUi> = emptyList()
) : BaseState

sealed class AllFavoriteCharactersEffect : BaseEffect

class AllFavoriteCharactersViewModel(
    private val characterInteractor: CharacterInteractor
) : BaseViewModel<AllFavoriteCharactersEvent, AllFavoriteCharactersState, AllFavoriteCharactersEffect>() {
    override fun setInitialState(): AllFavoriteCharactersState =
        AllFavoriteCharactersState(
            isLoading = true,
            error = null,
            bottomNavigationItems = getBottomNavigationItems(),
            favoriteCharacters = emptyList()
        )

    override fun handleEvents(event: AllFavoriteCharactersEvent) {
        when (event) {
            AllFavoriteCharactersEvent.Init -> {
                initView()
            }
        }
    }

    private fun initView() {
        setState {
            copy(
                isLoading = true
            )
        }
        viewModelScope.launch {
            characterInteractor.getFavoriteCharacters().collect {
                when (it) {
                    is GetFavoriteCharactersPartialState.Success -> {
                        setState {
                            copy(
                                isLoading = false,
                                favoriteCharacters = it.favoriteCharacters.toCharacterCardUiList()
                            )
                        }
                    }

                    is GetFavoriteCharactersPartialState.Failed -> {
                        setState {
                            copy(
                                isLoading = false,
                                error = AllFavoriteCharactersScreenErrorView(
                                    errorMessage = it.errorMessage,
                                    onRetryClick = { initView() }
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
            route = Screen.AllCharactersScreen.route,
            label = R.string.bottom_navigation_label_characters,
            iconData = IconData(
                iconResource = R.drawable.ic_superhero,
                contentDescription = R.string.bottom_navigation_label_characters
            )
        ),
        BottomBarItem(
            route = Screen.AllFavoriteCharactersScreen.route,
            label = R.string.bottom_navigation_label_favorites,
            iconData = IconData(
                iconResource = R.drawable.ic_favorite_filled,
                contentDescription = R.string.bottom_navigation_label_favorites
            )
        ),
        BottomBarItem(
            route = Screen.ProfileScreen.route,
            label = R.string.bottom_navigation_label_profile,
            iconData = IconData(
                iconResource = R.drawable.ic_person,
                contentDescription = R.string.bottom_navigation_label_profile
            )
        )
    )
}