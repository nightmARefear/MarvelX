package com.alexgreece45.marvelx.controllers.crc

import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.controllers.crc.model.MyZipEntry
import com.alexgreece45.marvelx.util.ResourceProvider
import org.bouncycastle.jcajce.provider.digest.Blake2b
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

/**
 * This controller contains the functionality to validate the CRC of the generated DEX files of this
 * application.
 */
interface DexCrcController {
    /**
     * Validates the overall CRC of the generated DEX files of this application. This is achieved by
     * retrieving all the [ZipEntry]s of the [ZipFile] of the application's code path, filtering
     * them by name ("`dex`"), ordering them based on their names in ascending order (firstly we
     * have replaced "classes" with "0" and ".dex" with ""), and then by calculating the signature.
     *
     * The signature is calculated by concatenating all the CRCs of the DEX files, and then applying
     * the `Blake2b` algorithm with the setting of `256` which indicates a 256-bit hash output.
     * Finally, the generated signature is compared against the stored one in the local strings
     * resource file, and if they match then that means that the application's code hasn't been
     * tampered with.
     *
     * @return `true` if the DEX CRC was successfully validated, otherwise `false`.
     */
    fun validateApplicationDEXCRC(): Boolean
}

internal class DexCrcControllerImpl(
    private val resourceProvider: ResourceProvider
) : DexCrcController {
    override fun validateApplicationDEXCRC(): Boolean {
        val zipFile = ZipFile(resourceProvider.getContext().packageCodePath)
        val zipEntries = zipFile.stream().iterator().asSequence().toList().filter {
            it.name.contains("dex")
        }

        val myZipEntries = zipEntries
            .map {
                MyZipEntry(
                    name = it.name
                        .replace("classes", "0")
                        .replace(".dex", ""),
                    crc = it.crc
                )
            }
            .sortedBy {
                it.name.toInt()
            }

        val concatenatedCrcs = buildString {
            myZipEntries.forEach {
                append(it.crc)
            }
        }

        return try {
            Security.addProvider(BouncyCastleProvider())
            val digest = Blake2b.Blake2b256()
            val blake2Hash = digest.digest(concatenatedCrcs.toByteArray(Charsets.UTF_8))
            val signature =
                android.util.Base64.encodeToString(blake2Hash, android.util.Base64.NO_WRAP)
            signature == resourceProvider.getString(R.string.dex_crc)
        } catch (e: Exception) {
            false
        }
    }
}