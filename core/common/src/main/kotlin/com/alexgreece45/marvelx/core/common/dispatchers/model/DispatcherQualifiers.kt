package com.alexgreece45.marvelx.core.common.dispatchers.model

enum class DispatcherQualifiers(val qualifier: String) {
    MAIN("MainDispatcher"),
    DEFAULT("DefaultDispatcher"),
    IO("IoDispatcher");

    override fun toString(): String {
        return qualifier
    }
}
