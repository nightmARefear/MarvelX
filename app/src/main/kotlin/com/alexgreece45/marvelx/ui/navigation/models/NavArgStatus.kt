package com.alexgreece45.marvelx.ui.navigation.models

import com.alexgreece45.marvelx.ui.navigation.NavArgHelper

/**
 * Navigation argument status. Useful when retrieving navigation argument values using
 * [NavArgHelper.getArgument]. This sealed interface allows the programmer to explicitly control
 * what happens when a navigation argument exists or not.
 */
sealed interface NavArgStatus<T> {
    /**
     * The argument doesn't exist.
     */
    data object NotExists : NavArgStatus<Nothing>

    /**
     * The argument exists and its value is [value].
     */
    data class Exists<T>(val value: T) : NavArgStatus<T>
}