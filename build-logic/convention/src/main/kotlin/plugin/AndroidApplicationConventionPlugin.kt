package plugin

import com.android.build.api.dsl.ApplicationExtension
import extensions.alias
import extensions.configureBuildTypes
import extensions.configureKotlinAndroid
import extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

internal class AndroidApplicationConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(libs.alias("android.application"))
                apply(libs.alias("kotlin.android"))
                apply(libs.alias("kotlin.compose"))
                apply(KotlinBasePlugin::class.java)
                apply(AndroidKoinConventionPlugin::class.java)
            }

            val applicationExtension = extensions.getByType<ApplicationExtension>()
            configureKotlinAndroid(applicationExtension)
            configureBuildTypes(applicationExtension)
        }
    }
}
