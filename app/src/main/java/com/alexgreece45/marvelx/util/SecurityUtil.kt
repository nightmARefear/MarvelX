package com.alexgreece45.marvelx.util

import com.alexgreece45.marvelx.BuildConfig
import com.lambdapioneer.argon2kt.Argon2Kt
import com.lambdapioneer.argon2kt.Argon2KtResult
import com.lambdapioneer.argon2kt.Argon2Mode
import logcat.logcat

object SecurityUtil {
    fun getHashedPasswordFromString(password: String): String {
        // Initializes Argon2Kt and loads the native library.
        val argon2Kt = Argon2Kt()

        // Hashes the given password.
        logcat { "(getHashedPasswordFromString) start of hashing..." }
        val hashResult: Argon2KtResult = argon2Kt.hash(
            mode = Argon2Mode.ARGON2_ID,
            password = password.toByteArray(),
            salt = BuildConfig.PASSWORD_SALT.toByteArray(),
            tCostInIterations = 20,
            mCostInKibibyte = 262144, // 256MB
            parallelism = 10,
            hashLengthInBytes = 128
        )
        logcat { "(getHashedPasswordFromString) end of hashing..." }

        return hashResult.encodedOutputAsByteArray().toString(Charsets.UTF_8)
            .also { logcat { "(getHashedPasswordFromString) finalPassword: $it" } }
    }
}