package com.alexgreece45.marvelx.ui.helper.model

/**
 * Defines a deep link action type by [name].
 */
sealed class DeepLinkActionType(open val name: String) {
    /**
     * Refers to viewing a screen.
     */
    data object View : DeepLinkActionType(name = "view")

    /**
     * Refers to viewing a screen that has actions that the user can take.
     */
    data object Action : DeepLinkActionType(name = "action")

    /**
     * Invalid deep link action type.
     */
    data class Invalid(override val name: String) : DeepLinkActionType(name = name)

    companion object {
        /**
         * Returns the [DeepLinkActionType] based on the given [name]. If it could not be found,
         * [Invalid] with the [name] is returned.
         */
        fun fromName(name: String): DeepLinkActionType =
            DeepLinkActionType::class.sealedSubclasses
                .mapNotNull { it.objectInstance }
                .firstOrNull { deepLinkActionType ->
                    deepLinkActionType.name == name
                } ?: Invalid(name = name)
    }
}