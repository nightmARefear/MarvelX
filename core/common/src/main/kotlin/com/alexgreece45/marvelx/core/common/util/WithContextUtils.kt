package com.alexgreece45.marvelx.core.common.util

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

suspend fun <T> withContextCatchable(
    context: CoroutineContext,
    onError: (errorMessage: String) -> T,
    logger: ((errorMessage: String) -> Unit)? = null,
    block: suspend CoroutineScope.() -> T
): T = try {
    withContext(context = context, block = block)
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) {
    val errorMessage = e.message.orEmpty()
    logger?.invoke(errorMessage)
    onError(errorMessage)
}