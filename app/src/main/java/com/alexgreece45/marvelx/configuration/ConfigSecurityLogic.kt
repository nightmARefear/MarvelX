package com.alexgreece45.marvelx.configuration

import com.alexgreece45.marvelx.util.constants.ConstantsSecurity

/**
 * Contains all the configuration options for security related operations.
 */
interface ConfigSecurityLogic {
    /**
     * Contains all the header names, in lowercase, that are used in the session signature
     * generation.
     */
    val sessionSignatureHeaders: List<String>
}

class ConfigSecurityLogicImpl : ConfigSecurityLogic {
    override val sessionSignatureHeaders: List<String>
        get() = listOf(ConstantsSecurity.HeaderName.UserAgent).map { it.name.lowercase() }
}