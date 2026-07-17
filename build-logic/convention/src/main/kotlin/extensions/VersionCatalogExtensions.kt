package extensions

import org.gradle.api.artifacts.ExternalModuleDependencyBundle
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.provider.Provider

internal fun VersionCatalog.alias(alias: String): String =
    findPlugin(alias).get().get().pluginId

internal fun VersionCatalog.library(alias: String): MinimalExternalModuleDependency =
    findLibrary(alias).get().get()

internal fun VersionCatalog.bundle(alias: String): Provider<ExternalModuleDependencyBundle?> =
    findBundle(alias).get()

internal fun VersionCatalog.version(alias: String): Int =
    findVersion(alias).get().toString().toInt()
