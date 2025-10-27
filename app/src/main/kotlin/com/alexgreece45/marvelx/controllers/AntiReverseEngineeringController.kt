package com.alexgreece45.marvelx.controllers

import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.util.ResourceProvider
import org.bouncycastle.jcajce.provider.digest.Blake2b
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

/**
 * Anti reverse engineering controller that contains many utility functions to detect if the
 * application has been reverse engineered.
 */
interface AntiReverseEngineeringController {
    /**
     * Validates the overall CRC of the generated DEX files of this application. This is achieved by
     * retrieving all the [ZipEntry]s of the [ZipFile] of the application's code that contain `dex`
     * in their name path, and then by calculating the signature.
     *
     * The signature is calculated by concatenating all the CRCs of the DEX files, and then applying
     * the `Blake2b` algorithm with the setting of `256` which indicates a 256-bit hash output.
     * Finally, the generated signature is compared against the stored one in the local strings
     * resource file, and if they match then that means that the application's code hasn't been
     * tampered with.
     *
     * @return `true` if the DEX CRC was successfully validated, otherwise `false`.
     */
    fun isApplicationDexCrcValid(): Boolean
}

internal class AntiReverseEngineeringControllerImpl(
    private val resourceProvider: ResourceProvider,
    private val appLogger: AppLogger
) : AntiReverseEngineeringController {
    override fun isApplicationDexCrcValid(): Boolean {
        return try {
            val zipFile = ZipFile(resourceProvider.getContext().packageCodePath)
            val zipEntries = zipFile.stream().iterator().asSequence().toList().filter {
                it.name.contains("dex")
            }

            val concatenatedCrcs = buildString {
                append(zipFile.getEntry("classes.dex").crc)
                for (i in 2..zipEntries.size) {
                    append(zipFile.getEntry("classes$i.dex").crc)
                }
            }

            Security.addProvider(BouncyCastleProvider())
            val digest = Blake2b.Blake2b256()
            val blake2Hash = digest.digest(concatenatedCrcs.toByteArray(Charsets.UTF_8))
            val signature = java.util.Base64.getEncoder().encodeToString(blake2Hash)
            signature == resourceProvider.getString(R.string.dex_crc)
        } catch (e: Exception) {
            appLogger.e("AntiReverseEngineeringController") { "e: $e" }
            false
        }
    }
}