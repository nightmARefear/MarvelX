package com.alexgreece45.marvelx.core.datastore.extensions

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.firstOrNull

internal suspend fun <T> DataStore<T?>.getData(): T? = data.firstOrNull()

internal suspend fun <T> DataStore<T?>.removeData(): T? = updateData { _ -> null }

internal suspend fun <T> DataStore<T?>.removeDataCatchable(): T? = try {
    updateData { _ -> null }
} catch (_: Exception) {
    null
}

internal suspend fun <T> DataStore<T>.updateDataCatchable(transform: suspend (T) -> T): Result<T> =
    try {
        Result.success(updateData(transform = transform))
    } catch (e: Exception) {
        Result.failure(e)
    }