package plugin

import com.android.build.api.dsl.ApplicationExtension
import extensions.configureAndroidCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

internal class AndroidApplicationComposeConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(AndroidApplicationConventionPlugin::class.java)
            }

            val applicationExtension = extensions.getByType<ApplicationExtension>()
            applicationExtension.apply {
                buildFeatures {
                    buildConfig = true
                }
            }
            configureAndroidCompose(applicationExtension)

            val composeExtension = extensions.getByType<ComposeCompilerGradlePluginExtension>()
            configureCompose(composeExtension)
        }
    }

    private fun Project.configureCompose(
        extension: ComposeCompilerGradlePluginExtension,
    ) {
        extension.apply {
            val buildDir = layout.buildDirectory.get().asFile
            val relativePath = projectDir.relativeTo(rootDir)
            val metricsFolder = buildDir.resolve("compose-metrics").resolve(relativePath)
            val reportsFolder = buildDir.resolve("compose-reports").resolve(relativePath)

            // use -Pandroidx.enableComposeCompilerMetrics=true to enable compose compiler metrics
            metricsDestination.set(metricsFolder)

            // use -Pandroidx.enableComposeCompilerReports=true to enable compose compiler reports
            reportsDestination.set(reportsFolder)
        }
    }
}