package com.alexgreece45.marvelx.core.common.config.model

sealed class AppBuildType(val key: String) {
    data object DEBUG : AppBuildType(key = "debug")
    data object MOCK : AppBuildType(key = "mock")
    data object RELEASE : AppBuildType(key = "release")
}