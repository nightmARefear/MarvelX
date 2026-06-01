package com.alexgreece45.marvelx.core.common.extensions

/**
 * Returns the logger tag for classes.
 */
val Any.LOGGER_TAG: String
    get() = this::class.java.simpleName.removeSuffix("Impl")
