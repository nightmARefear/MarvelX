package com.alexgreece45.marvelx.util

import android.util.Base64
import com.alexgreece45.marvelx.BuildConfig
import com.lambdapioneer.argon2kt.Argon2Kt
import com.lambdapioneer.argon2kt.Argon2KtResult
import com.lambdapioneer.argon2kt.Argon2Mode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import logcat.logcat
import java.security.MessageDigest
import java.security.SecureRandom
import kotlin.coroutines.CoroutineContext

object SecurityUtil { // TODO: utilize coroutines.
    /**
     * Gets the hashed version of the given password. The `Argon2ID` algorithm is used, alongside
     * a strong iteration count.
     */
    suspend fun getHashedPasswordFromString(
        defaultDispatcher: CoroutineContext,
        password: String
    ): Flow<String> = flow {
        // Initializes Argon2Kt and loads the native library.
        val argon2Kt = Argon2Kt()

        // Hashes the given password.
        logcat { "(getHashedPasswordFromString) start of hashing..." }
        val hashResult: Argon2KtResult = argon2Kt.hash(
            mode = Argon2Mode.ARGON2_ID,
            password = password.toByteArray(),
            salt = BuildConfig.PASSWORD_SALT.toByteArray(),
            tCostInIterations = 10,
            mCostInKibibyte = 131072, // 128MB
            parallelism = 10,
            hashLengthInBytes = 128
        )
        logcat { "(getHashedPasswordFromString) end of hashing..." }

        emit(hashResult.encodedOutputAsByteArray().toString(Charsets.UTF_8))
    }.flowOn(defaultDispatcher)
}
