package com.alexgreece45.marvelx.core.common.extensions

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlin.coroutines.CoroutineContext

fun <T> Flow<T>.safeAsync(with: (Throwable) -> (T)): Flow<T> {
    return this.flowOn(Dispatchers.IO).catch { emit(with(it)) }
}

fun <T> Flow<T>.safeAsync(ioDispatcher: CoroutineContext, with: (Throwable) -> (T)): Flow<T> {
    return this.flowOn(ioDispatcher).catch { emit(with(it)) }
}