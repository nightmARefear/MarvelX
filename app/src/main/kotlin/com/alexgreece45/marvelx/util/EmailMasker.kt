package com.alexgreece45.marvelx.util

/**
 * Masks email addresses for display, so that the user can recognize their mailbox without the
 * full address being shown (e.g., on the OTP screen: `alex@gmail.com` -> `a***x@gmail.com`).
 */
object EmailMasker {

    private const val MASK = "***"

    /**
     * Returns the masked version of the given [email], or `null` if it isn't a `local@domain`
     * address.
     *
     * The first and last characters of the local part are kept (only the first one, if it's up to
     * two characters long), and the domain is kept as-is.
     */
    fun mask(email: String): String? {
        val trimmedEmail = email.trim()
        val separatorIndex = trimmedEmail.lastIndexOf('@')
        if (separatorIndex <= 0 || separatorIndex == trimmedEmail.lastIndex) return null

        val localPart = trimmedEmail.substring(0, separatorIndex)
        val domain = trimmedEmail.substring(separatorIndex + 1)
        val maskedLocalPart = if (localPart.length <= 2) {
            "${localPart.first()}$MASK"
        } else {
            "${localPart.first()}$MASK${localPart.last()}"
        }

        return "$maskedLocalPart@$domain"
    }
}
