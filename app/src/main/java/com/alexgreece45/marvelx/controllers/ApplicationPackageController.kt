package com.alexgreece45.marvelx.controllers

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.util.Base64
import com.alexgreece45.marvelx.util.ResourceProvider
import java.security.MessageDigest
import javax.inject.Inject

interface ApplicationPackageController {
    /**
     * Returns all the application package signatures concatenated with no spaces between as a [String].
     * If no signatures were found, or an error occurred, an empty [String] is returned.
     */
    fun getSignature(): String
}

class ApplicationPackageControllerImpl @Inject constructor(
    private val resourceProvider: ResourceProvider,
    private val logger: AppLoggerController
) : ApplicationPackageController {
    override fun getSignature(): String {
        try {
            val packageInfo: PackageInfo
            val signatures: List<String>
            if (SDK_INT < Build.VERSION_CODES.P) {
                packageInfo = resourceProvider.getContext().packageManager.getPackageInfo(
                    resourceProvider.getContext().packageName,
                    PackageManager.GET_SIGNATURES
                )
                signatures = packageInfo.signatures.map {
                    val messageDigest = MessageDigest.getInstance("SHA").apply {
                        update(it.toByteArray())
                    }
                    Base64.encodeToString(messageDigest.digest(), Base64.DEFAULT).trim()
                }
            } else {
                packageInfo = resourceProvider.getContext().packageManager.getPackageInfo(
                    resourceProvider.getContext().packageName,
                    PackageManager.GET_SIGNING_CERTIFICATES
                )
                signatures = if (packageInfo.signingInfo.hasMultipleSigners()) {
                    packageInfo.signingInfo.apkContentsSigners.map {
                        val messageDigest = MessageDigest.getInstance("SHA").apply {
                            update(it.toByteArray())
                        }
                        Base64.encodeToString(messageDigest.digest(), Base64.DEFAULT).trim()
                    }
                } else {
                    packageInfo.signingInfo.signingCertificateHistory.map {
                        val messageDigest = MessageDigest.getInstance("SHA").apply {
                            update(it.toByteArray())
                        }
                        Base64.encodeToString(messageDigest.digest(), Base64.DEFAULT).trim()
                    }
                }
            }
            return signatures.joinToString("")
        } catch (e: Exception) {
            logger.e { "${javaClass.simpleName}: e" }
        }
        return ""
    }
}