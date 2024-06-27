package com.alexgreece45.marvelx.domain.model_api.user.profileDetails

/**
 * All the possible error types when retrieving the profile details of a user.
 */
sealed class ProfileDetailsErrorType(val key: String) {
    /**
     * The user is blocked.
     */
    data object Blocked : ProfileDetailsErrorType("blocked")

    /**
     * The user does not exist.
     */
    data object NotExists : ProfileDetailsErrorType("not_exists")

    /**
     * Something went wrong in the server.
     */
    data object InternalServerError : ProfileDetailsErrorType("internal_server_error")

    companion object {
        /**
         * Attempts to retrieve the corresponding [ProfileDetailsErrorType] based on the given [key].
         * If the retrieval fails, then [NoSuchElementException] is thrown.
         */
        fun fromKey(key: String): ProfileDetailsErrorType =
            ProfileDetailsErrorType::class.sealedSubclasses
                .mapNotNull { it.objectInstance }
                .first { it.key == key }
    }
}
