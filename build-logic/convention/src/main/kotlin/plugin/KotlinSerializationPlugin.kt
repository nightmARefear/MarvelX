package plugin

import extensions.alias
import extensions.implementation
import extensions.library
import extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal class KotlinSerializationPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(libs.alias("kotlin.serialization"))

            dependencies {
                implementation(libs.library("kotlin.serialization"))
                implementation(libs.library("kotlinx.serialization.converter"))
            }
        }
    }
}