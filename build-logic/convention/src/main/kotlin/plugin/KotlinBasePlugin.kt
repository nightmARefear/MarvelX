package plugin

import extensions.javaVersion
import extensions.jvmTarget
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

internal class KotlinBasePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            configureJvmToolChain()
            configureKotlinCompilerOptions(jvmTarget)
        }
    }
}

private fun Project.configureJvmToolChain() {
    configure<JavaPluginExtension> {
        targetCompatibility = javaVersion
    }

    extensions.findByType<KotlinProjectExtension>()?.apply {
        jvmToolchain(javaVersion.majorVersion.toInt())
    }
}

private fun Project.configureKotlinCompilerOptions(jvmTarget: JvmTarget) {
    tasks.withType<KotlinCompilationTask<*>>().configureEach {
        compilerOptions {
            when (this) {
                is KotlinJvmCompilerOptions -> {
                    this.jvmTarget.set(jvmTarget)
                }
            }
        }
    }
}
