package plugin

import extensions.implementation
import extensions.library
import extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal class AndroidKoinConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(KoinConventionPlugin::class.java)

            dependencies {
                implementation(libs.library("koin.android"))
                implementation(libs.library("koin.androidx.compose"))
                implementation(libs.library("koin.androidx.navigation"))
            }
        }
    }
}