package com.alexgreece45.marvelx.navigation

sealed class Screen(
    val route: String
) {
    object AllCharactersScreens : Screen(route = "allCharactersScreen")

    object CharacterScreen : Screen(route = "characterScreen")

    object AllCharacterComicsScreen : Screen(route = "allCharacterComicsScreen")

    object ComicScreen : Screen(route = "comicScreen")

    object AllFavoriteCharactersScreen : Screen(route = "allFavoriteCharactersScreen")

    object ProfileScreen : Screen(route = "profileScreen")

    object SignUpScreen : Screen(route = "signUpScreen")

    companion object {
        fun getAfterSuccessfulLoginScreen(): Screen = AllCharactersScreens

        fun getBottomNavigationScreens(): List<Screen> =
            listOf(AllCharactersScreens, AllFavoriteCharactersScreen, ProfileScreen)
    }
}
