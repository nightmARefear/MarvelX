package plugin

import com.android.build.api.dsl.AndroidSourceSet
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import extensions.alias
import extensions.implementation
import extensions.implementationPlatform
import extensions.ksp
import extensions.library
import extensions.libs
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

internal class KoinConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(libs.alias("ksp"))

            dependencies {
                implementationPlatform(libs.library("koin.bom"))
                implementation(libs.library("koin.annotations"))
                implementation(libs.library("koin.core"))
                ksp(libs.library("koin.ksp.compiler"))
            }

            when {
                pluginManager.hasPlugin(libs.alias("android.application")) ->
                    configure<ApplicationExtension> { addVariantOptions(this.sourceSets) }

                pluginManager.hasPlugin(libs.alias("android.library")) ->
                    configure<LibraryExtension> { addVariantOptions(this.sourceSets) }
            }
        }
    }

    private fun addVariantOptions(sourceSets: NamedDomainObjectContainer<out AndroidSourceSet>) {
        apply {
            sourceSets.all {
                kotlin.srcDir("build/generated/ksp/$name/kotlin")
            }
        }
    }
}
