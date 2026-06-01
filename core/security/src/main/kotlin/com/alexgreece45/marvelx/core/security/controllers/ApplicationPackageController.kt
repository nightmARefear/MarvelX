package com.alexgreece45.marvelx.core.security.controllers

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.util.Base64
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import org.koin.core.annotation.Single
import java.security.MessageDigest

internal interface ApplicationPackageController {

    /**
     * Returns all the application package signatures as a [List].
     * If no signatures were found, or an error occurred, an empty [String] is returned.
     */
    fun getApplicationSignatures(): List<String>

    /**
     * Checks whether the application allows debugging of its code , even when installed on a
     * non-development system. Comes from `android:debuggable` of the `<application>` tag in
     * `AndroidManifest`.
     *
     * @return Returns `true` if the application allows debugging of its code, otherwise `false`.
     */
    fun isAppDebuggable(): Boolean
}

@Single
internal class ApplicationPackageControllerImpl(
    private val context: Context,
    private val logger: AppLogger
) : ApplicationPackageController {

    override fun getApplicationSignatures(): List<String> { // TODO: To be updated.
        try {
            val packageInfo: PackageInfo
            val signatures: List<String>
            if (SDK_INT < Build.VERSION_CODES.P) {
                packageInfo = context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_SIGNATURES
                )
                signatures = packageInfo.signatures?.map {
                    val messageDigest = MessageDigest.getInstance("SHA-256").apply {
                        update(it.toByteArray())
                    }
                    Base64.encodeToString(messageDigest.digest(), Base64.DEFAULT).trim()
                } ?: emptyList()
            } else {
                packageInfo = context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_SIGNING_CERTIFICATES
                )
                signatures = if (packageInfo.signingInfo?.hasMultipleSigners() == true) {
                    packageInfo.signingInfo?.apkContentsSigners?.map {
                        val messageDigest = MessageDigest.getInstance("SHA-256").apply {
                            update(it.toByteArray())
                        }
                        Base64.encodeToString(messageDigest.digest(), Base64.DEFAULT).trim()
                    } ?: emptyList()
                } else {
                    packageInfo.signingInfo?.signingCertificateHistory?.map {
                        val messageDigest = MessageDigest.getInstance("SHA-256").apply {
                            update(it.toByteArray())
                        }
                        Base64.encodeToString(messageDigest.digest(), Base64.DEFAULT).trim()
                    } ?: emptyList()
                }
            }
            return signatures
        } catch (e: Exception) {
            logger.e(javaClass.simpleName) { "e: $e" }
        }
        return emptyList()
    }

    override fun isAppDebuggable(): Boolean =
        0 != context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE
}