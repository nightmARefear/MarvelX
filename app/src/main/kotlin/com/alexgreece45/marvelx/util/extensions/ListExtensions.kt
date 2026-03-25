package com.alexgreece45.marvelx.util.extensions

fun <T : Any, V : Any> List<T>.transform(transformation: (T) -> V?): List<V>? {
    return this.map { transformation(it) ?: return null }
}
