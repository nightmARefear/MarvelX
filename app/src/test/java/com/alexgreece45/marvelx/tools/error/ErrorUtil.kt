package com.alexgreece45.marvelx.tools.error

/**
 * Contains utility variables, and methods for performing unit tests.
 */
object ErrorUtil {
    const val errorMessage = "An unknown error has occurred!"
    val runtimeException = RuntimeException(errorMessage)
}
