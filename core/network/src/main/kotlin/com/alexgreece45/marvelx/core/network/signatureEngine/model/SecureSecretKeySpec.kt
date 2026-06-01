package com.alexgreece45.marvelx.core.network.signatureEngine.model

import java.io.FileOutputStream
import java.security.MessageDigest
import java.security.spec.KeySpec
import java.util.Locale
import javax.crypto.SecretKey

/**
 * A secure implementation of [javax.crypto.spec.SecretKeySpec] that overrides the [destroy] method to properly erase
 * the [key] from the memory.
 */
internal class SecureSecretKeySpec(key: ByteArray?, algorithm: String) : KeySpec, SecretKey {
    private var key: ByteArray?
    private val algorithm: String

    init {
        require(key != null && algorithm.isNotBlank()) { "Missing argument" }
        this.key = key.clone()
        this.algorithm = algorithm
    }

    override fun getAlgorithm(): String = algorithm

    override fun getFormat(): String = "RAW"

    /**
     * Returns a copy of the [key]. Make sure to clear the returned byte array when no longer needed.
     */
    override fun getEncoded(): ByteArray {
        if (key == null) throw NullPointerException()
        return key!!.clone()
    }

    /**
     * Overwrites the [key] with dummy data, flushes the output stream, and explicitly calls the
     * garbage collector to ensure this copy is no longer present in memory.
     */
    override fun destroy() {
        if (isDestroyed) return

        val nonSecret = "RuntimeException".toByteArray(charset("ISO-8859-1"))
        key?.indices?.forEach { index ->
            key!![index] = nonSecret[index % nonSecret.size]
        }

        val out = FileOutputStream("/dev/null")
        out.write(key)
        out.flush()
        out.close()
        key = null
        System.gc()
    }

    override fun isDestroyed(): Boolean = key == null

    override fun hashCode(): Int {
        var retval = 0
        this.key?.forEachIndexed { index, byte ->
            retval += byte * (index + 1)
        }
        return if (algorithm.equals("TripleDES", true)) {
            retval xor "desede".hashCode()
        } else {
            retval xor algorithm.lowercase(Locale.ENGLISH).hashCode()
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SecretKey) return false

        val thatAlg = other.algorithm
        if (!(thatAlg.equals(this.algorithm, ignoreCase = true))) {
            if ((!(thatAlg.equals("DESede", ignoreCase = true))
                        || !(algorithm.equals("TripleDES", ignoreCase = true)))
                && (!(thatAlg.equals("TripleDES", ignoreCase = true))
                        || !(algorithm.equals("DESede", ignoreCase = true)))
            ) return false
        }

        val thatKey = other.encoded
        return MessageDigest.isEqual(this.key, thatKey)
    }
}