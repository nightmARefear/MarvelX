package com.alexgreece45.marvelx.common_ui.extensions

import android.util.Patterns

/**
 * Checks if the given [String] is a valid email address.
 *
 * @return Returns `true` if the given [String] is a valid email address, otherwise `false`.
 */
fun String.isEmail(): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}
