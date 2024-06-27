package com.alexgreece45.marvelx.util.extensions

import java.nio.ByteBuffer

/**
 * Converts this instance to a [ByteArray].
 */
internal fun ByteBuffer.toByteArray(): ByteArray {
    rewind()
    return ByteArray(remaining()).also {
        get(it)
    }
}
