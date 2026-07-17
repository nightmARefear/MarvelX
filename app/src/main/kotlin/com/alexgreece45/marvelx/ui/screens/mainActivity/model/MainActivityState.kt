package com.alexgreece45.marvelx.ui.screens.mainActivity.model

import com.alexgreece45.marvelx.mvi.BaseState

data class MainActivityState(
    val flowStarted: Boolean = false,
    val currentRoute: String = ""
) : BaseState