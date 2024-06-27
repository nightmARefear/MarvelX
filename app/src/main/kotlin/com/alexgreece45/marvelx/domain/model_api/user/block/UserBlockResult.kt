package com.alexgreece45.marvelx.domain.model_api.user.block

/**
 * The result of blocking a user.
 */
sealed class UserBlockResult(val name: String) {
    /**
     * The user is trying to block themselves.
     */
    data object SameUser : UserBlockResult("same_user")

    /**
     * An error occurred whilst blocking another user.
     */
    data object Failed : UserBlockResult("failed")

    companion object {
        /**
         * Attempts to retrieve the corresponding [UserBlockResult] based on the given [name], and
         * if it fails to do so, [NoSuchElementException] is thrown.
         */
        fun fromName(name: String): UserBlockResult = UserBlockResult::class.sealedSubclasses
            .mapNotNull { it.objectInstance }
            .first { it.name == name }
    }
}
