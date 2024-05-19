package com.alexgreece45.marvelx.ui.navigation

import com.alexgreece45.marvelx.ui.navigation.models.NavArgStatus

/**
 * Navigation argument helper since compose as of version `2.7.7` doesn't support null values as
 * navigation arguments.
 */
object NavArgHelper {
    /**
     * All the user-set arguments.
     */
    private val arguments: MutableMap<String, Any?> = mutableMapOf()

    /**
     * Sets an argument with the given [navItem] as its key and [value] as its value.
     */
    fun setArgument(navItem: NavItem, value: Any?) {
        arguments[navItem.key] = value
    }

    /**
     * Retrieves the value of the given argument key. If the argument doesn't exist, then
     * [NavArgStatus.NotExists] is returned, otherwise [NavArgStatus.Exists] with the value within
     * it.
     *
     * Also, the argument is immediately removed if it exists.
     */
    fun getArgument(navItem: NavItem): NavArgStatus {
        return when {
            arguments.containsKey(navItem.key).not() -> {
                NavArgStatus.NotExists
            }

            else -> NavArgStatus.Exists(arguments[navItem.key])
                .also { arguments.remove(navItem.key) }
        }
    }
}