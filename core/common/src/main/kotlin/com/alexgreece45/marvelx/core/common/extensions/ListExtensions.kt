package com.alexgreece45.marvelx.core.common.extensions

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

fun <T> List<T>?.toImmutableListOrEmpty(): ImmutableList<T> =
    this?.toImmutableList() ?: persistentListOf()