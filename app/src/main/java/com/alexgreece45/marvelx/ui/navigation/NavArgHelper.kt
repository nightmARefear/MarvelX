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
     * Retrieves the value of the given argument key and attempts to typecast it to the type [T].
     * If the argument doesn't exist or the typecast failed, then [NavArgStatus.NotExists] is
     * returned, otherwise [NavArgStatus.Exists] with the value within it.
     *
     * Also, the argument is immediately removed if it exists.
     */
    fun <T> getArgument(navItem: NavItem): NavArgStatus<out T> {
        return when {
            arguments.containsKey(navItem.key).not() -> {
                NavArgStatus.NotExists
            }

            else -> try {
                @Suppress("UNCHECKED_CAST")
                NavArgStatus.Exists(arguments[navItem.key] as T)
                    .also { arguments.remove(navItem.key) }
            } catch (e: ClassCastException) {
                NavArgStatus.NotExists
            }
        }
    }
}