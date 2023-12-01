package com.alexgreece45.marvelx.controllers

import android.os.Build
import com.alexgreece45.marvelx.configuration.AppBuildType
import com.alexgreece45.marvelx.configuration.ConfigLogic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

interface SecurityController {
    /**
     * Checks whether the package signature is valid, and returns `true` if it, otherwise `false`.
     */
    fun isPackageSignatureValid(): Boolean

    /**
     * Checks whether the app is running on an emulator, and returns `true` if it is otherwise
     * `false`.
     *
     * ### Note: This condition doesn't guarantee that it can always be detected if the app is running on an emulator.
     */
    fun isAppRunningOnEmulator(): Boolean

    /**
     * Checks whether the device is rooted, and returns `true` if it is otherwise `false`.
     *
     * ### Note: This condition doesn't guarantee that a rooted device can always be detected.
     */
    fun isDeviceRooted(): Boolean

    /**
     * Checks whether the device has root cloaking apps installed, and returns `true` if it has
     * otherwise `false`.
     *
     * ### Note: This condition doesn't guarantee that installed root cloaking apps can always be detected.
     */
    fun areRootCloakingAppsInstalled(): Boolean

    /**
     * Checks whether this application is running on a secure device and/or if it has been tampered with.
     *
     * ### Note: This condition doesn't guarantee that the device is secure, or that the application
     * hasn't been tampered with.
     */
    fun isApplicationSecure(): Flow<Boolean>
}

class SecurityControllerImpl @Inject constructor(
    private val configLogic: ConfigLogic,
    private val rootController: RootController,
    private val applicationPackageController: ApplicationPackageController
) : SecurityController {
    private val isApplicationSecure: Boolean
        get() = isPackageSignatureValid()
                && !isAppRunningOnEmulator()
                && !isDeviceRooted()
                && !areRootCloakingAppsInstalled()

    override fun isPackageSignatureValid(): Boolean {
        if (configLogic.appBuildType != AppBuildType.RELEASE) return true
        return applicationPackageController.getSignature() == configLogic.packageSignature
    }

    override fun isAppRunningOnEmulator(): Boolean =
        configLogic.appBuildType == AppBuildType.RELEASE
                && (
                    // Android SDK emulator
                    (Build.MANUFACTURER == "Google" && Build.BRAND == "google"
                        && (Build.FINGERPRINT.startsWith("google/sdk_gphone")
                                && Build.PRODUCT.startsWith("sdk_gphone")
                                && Build.MODEL.startsWith("sdk_gphone")
                            )
                    )
                    || Build.FINGERPRINT.startsWith("generic")
                    || Build.FINGERPRINT.startsWith("unknown")
                    || Build.MODEL.startsWith("google_sdk")
                    || Build.MODEL.startsWith("Emulator")
                    || Build.MODEL.contains("Android SDK built for x86")
                    // Bluestacks
                    || Build.BOARD.equals("QC_Reference_Phone") && !Build.MANUFACTURER.equals("Xiaomi", ignoreCase = true)
                    || Build.MANUFACTURER.contains("Genymotion")
                    || Build.HOST.startsWith("Build")
                    // MSI App Player
                    || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
                    || Build.PRODUCT == "google_sdk"
                )

    override fun isDeviceRooted(): Boolean =
        configLogic.appBuildType == AppBuildType.RELEASE
                && rootController.isRooted()

    override fun areRootCloakingAppsInstalled(): Boolean =
        configLogic.appBuildType == AppBuildType.RELEASE
                && rootController.areRootCloakingAppsInstalled()

    override fun isApplicationSecure(): Flow<Boolean> = flow {
        emit(isApplicationSecure)
    }.flowOn(Dispatchers.IO)
}