package com.alexgreece45.marvelx.configuration

import com.alexgreece45.marvelx.core.common.config.ConfigLogic
import com.alexgreece45.marvelx.core.common.config.SecurityConfigLogic
import com.alexgreece45.marvelx.core.common.config.model.AppBuildType
import com.alexgreece45.marvelx.core.common.constants.ConstantsSecurity
import org.koin.core.annotation.Single

@Single
internal class SecurityConfigLogicImpl(
    private val configLogic: ConfigLogic
) : SecurityConfigLogic {

    override val useNetworkLogger: Boolean
        get() = configLogic.appBuildType !is AppBuildType.RELEASE

    override val sessionSignatureHeaders: List<String>
        get() = listOf(ConstantsSecurity.HeaderName.UserAgent).map { it.name.lowercase() }

    override val enableStrictMode: Boolean
        get() = configLogic.appBuildType is AppBuildType.RELEASE

    override val isAutoLogoutEnabled: Boolean
        get() = configLogic.appBuildType is AppBuildType.RELEASE

    override val packageSignature: String
        get() = "M2wqQ4AaGrAm/ch656I3n1LCOfcsCiSDsboMQs+vGaY="
}