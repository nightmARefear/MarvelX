package com.alexgreece45.marvelx.domain.model_api.user.block

/**
 * The result of blocking a user.
 */
sealed class UserBlockResultType(val name: String) {

    /**
     * The user is trying to block themselves.
     */
    data object SameUser : UserBlockResultType("same_user")

    /**
     * An error occurred whilst blocking another user.
     */
    data object Failed : UserBlockResultType("failed")

    companion object {

        /**
         * Attempts to retrieve the corresponding [UserBlockResultType] based on the given [name], and
         * if it fails to do so, [NoSuchElementException] is thrown.
         */
        fun fromName(name: String): UserBlockResultType =
            listOf(SameUser, Failed).first { it.name == name }
    }
}
