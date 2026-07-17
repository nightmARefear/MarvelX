package plugin

import com.android.build.api.dsl.LibraryExtension
import extensions.alias
import extensions.configureBuildTypes
import extensions.configureKotlinAndroid
import extensions.implementation
import extensions.library
import extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

internal class AndroidLibraryConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(libs.alias("android.library"))
                apply(libs.alias("kotlin.android"))
            }

            val extension = extensions.getByType<LibraryExtension>()
            configureKotlinAndroid(extension)
            configureBuildTypes(extension)

            dependencies {
                implementation(libs.library("androidx.core"))

                // Enforces coroutine versions for all modules.
                implementation(libs.library("kotlinx.coroutines.core"))
                implementation(libs.library("kotlinx.coroutines.android"))
            }
        }
    }
}
