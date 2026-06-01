package com.alexgreece45.marvelx.ui.navigation

import com.alexgreece45.marvelx.ui.navigation.models.NavArg
import com.alexgreece45.marvelx.ui.navigation.models.NavArgStatus
import java.util.concurrent.ConcurrentMap

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
     * Sets an argument with the given [navArg] as its key and [value] as its value.
     */
    fun setArgument(navArg: NavArg, value: Any?) {
        arguments[navArg.key] = value
    }

    /**
     * Retrieves the value of the given argument key and attempts to typecast it to the type [T].
     * If the argument doesn't exist or the typecast failed, then [NavArgStatus.NotExists] is
     * returned, otherwise [NavArgStatus.Exists] with the value within it.
     *
     * Also, the argument is immediately removed if it exists.
     */
    fun <T> getArgument(navArg: NavArg): NavArgStatus<out T> {
        return when {
            arguments.containsKey(navArg.key) -> try {
                @Suppress("UNCHECKED_CAST")
                NavArgStatus.Exists(arguments[navArg.key] as T)
                    .also { arguments.remove(navArg.key) }
            } catch (_: ClassCastException) {
                NavArgStatus.NotExists
            }

            else -> NavArgStatus.NotExists
        }
    }
}
