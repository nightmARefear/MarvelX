package com.alexgreece45.marvelx.tools.extensions

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

fun <T> T.toFlow(): StateFlow<T> =
    MutableStateFlow(this).asStateFlow()
