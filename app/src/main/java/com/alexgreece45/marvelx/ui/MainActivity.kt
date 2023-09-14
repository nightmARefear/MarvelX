package com.alexgreece45.marvelx.ui

import android.os.Bundle
import android.view.Surface
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.alexgreece45.marvelx.navigation.Screen
import com.alexgreece45.marvelx.ui.screens.allCharactersScreen.AllCharactersScreen
import com.alexgreece45.marvelx.ui.screens.characterScreen.CharacterScreen
import com.alexgreece45.marvelx.ui.theme.MarvelXTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MarvelXTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = Screen.getAfterSuccessfulLoginScreen().route
                    ) {
                        navigation(
                            startDestination = Screen.getAfterSuccessfulLoginScreen().route,
                            route = Screen.getAfterSuccessfulLoginScreen().route
                        ) {
                            composable(Screen.AllCharactersScreens.route) {
                                AllCharactersScreen(
                                    navController = navController
                                )
                            }

                            composable(
                                route = Screen.CharacterScreen.route + "?characterId={characterId}",
                                arguments = listOf(
                                    navArgument("characterId") {
                                        type = NavType.IntType
                                        defaultValue = -1
                                    }
                                )
                            ) { backStackEntry ->
                                CharacterScreen(
                                    navController = navController,
                                    characterId = backStackEntry.arguments?.getInt("characterId")
                                        ?: -1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}