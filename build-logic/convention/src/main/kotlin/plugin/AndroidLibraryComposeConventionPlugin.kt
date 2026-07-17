package plugin

import com.android.build.gradle.LibraryExtension
import extensions.alias
import extensions.configureAndroidCompose
import extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

internal class AndroidLibraryComposeConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(AndroidLibraryConventionPlugin::class.java)
                apply(libs.alias("kotlin.compose"))
            }

            val libraryExtension= extensions.getByType<LibraryExtension>()
            configureAndroidCompose(libraryExtension)
        }
    }
}
