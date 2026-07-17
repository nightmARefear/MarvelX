package com.alexgreece45.marvelx.core.datastore.extensions

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.firstOrNull

internal suspend fun <T> DataStore<Preferences>.getValue(
    key: Preferences.Key<T>,
    defaultValue: T?
): T? = data.firstOrNull()?.get(key) ?: defaultValue

internal suspend fun <T> DataStore<Preferences>.editValue(
    key: Preferences.Key<T>,
    value: T
): Preferences = edit { preferences -> preferences[key] = value }

internal suspend fun <T> DataStore<Preferences>.removeKey(
    key: Preferences.Key<T>
): Preferences = edit { preferences -> preferences.remove(key) }

internal suspend fun DataStore<Preferences>.removeKeys(
    vararg keys: Preferences.Key<*>
): Preferences = edit { preferences -> keys.forEach { key -> preferences.remove(key) } }