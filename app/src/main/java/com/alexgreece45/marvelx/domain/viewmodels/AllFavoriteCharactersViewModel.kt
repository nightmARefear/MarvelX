package com.alexgreece45.marvelx.domain.viewmodels

import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.navigation.Screen
import com.alexgreece45.marvelx.ui.BottomBarItem
import com.alexgreece45.marvelx.ui.IconData
import com.alexgreece45.marvelx.ui.base.BaseEffect
import com.alexgreece45.marvelx.ui.base.BaseEvent
import com.alexgreece45.marvelx.ui.base.BaseState
import com.alexgreece45.marvelx.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

sealed class AllFavoriteCharactersEvent : BaseEvent {
    object InitView : AllFavoriteCharactersEvent()
}

data class AllFavoriteCharactersState(
    val isLoading: Boolean = false,

    // Data.
    val bottomNavigationItems: List<BottomBarItem> = emptyList(),
) : BaseState

sealed class AllFavoriteCharactersEffect : BaseEffect {

}

@HiltViewModel
class AllFavoriteCharactersViewModel @Inject constructor(
) : BaseViewModel<AllFavoriteCharactersEvent, AllFavoriteCharactersState, AllFavoriteCharactersEffect>() {
    override fun setInitialState(): AllFavoriteCharactersState = AllFavoriteCharactersState(
        isLoading = false,
        bottomNavigationItems = getBottomNavigationItems(),
    )

    override fun handleEvents(event: AllFavoriteCharactersEvent) {
        when (event) {
            AllFavoriteCharactersEvent.InitView -> {
                initView()
            }
        }
    }

    private fun initView() {
    }

    private fun getBottomNavigationItems(): List<BottomBarItem> = listOf(
        BottomBarItem(
            route = Screen.BottomNavigationScreens.AllCharactersScreen.route,
            label = R.string.bottom_navigation_label_characters,
            iconData = IconData(
                iconResource = R.drawable.ic_superhero_32,
                contentDescription = R.string.bottom_navigation_label_characters
            )
        ),
        BottomBarItem(
            route = Screen.BottomNavigationScreens.AllFavoriteCharactersScreen.route,
            label = R.string.bottom_navigation_label_favorites,
            iconData = IconData(
                iconResource = R.drawable.ic_favorite_filled_24,
                contentDescription = R.string.bottom_navigation_label_favorites
            )
        ),
        BottomBarItem(
            route = Screen.BottomNavigationScreens.ProfileScreen.route,
            label = R.string.bottom_navigation_label_profile,
            iconData = IconData(
                iconResource = R.drawable.ic_person_32,
                contentDescription = R.string.bottom_navigation_label_profile
            )
        )
    )
}