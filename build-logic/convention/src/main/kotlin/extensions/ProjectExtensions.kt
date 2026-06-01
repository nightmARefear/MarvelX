package extensions

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

internal val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal val Project.javaVersion: JavaVersion
    get() {
        val javaVersionInt = libs.version("java.version")
        return JavaVersion.values()[javaVersionInt - 1]
    }

internal val Project.jvmTarget: JvmTarget
    get() {
        val javaVersion = libs.findVersion("java.version").get().toString()
        return JvmTarget.values().first { target -> target.toString().contains(javaVersion) }
    }

internal fun Project.configureAndroidCompose(commonExtension: CommonExtension<*, *, *, *, *, *>) {
    commonExtension.apply {
        buildFeatures {
            compose = true
        }

        dependencies {
            implementationPlatform(libs.library("androidx.compose.bom"))

            implementation(libs.bundle("app.ui"))
            implementation(libs.library("androidx.compose.ui.tooling")) // Added to fix render issue (ClassNotFoundException: ComposeViewAdapter)

            debugImplementation(libs.bundle("app.ui.debug"))
            mockImplementation(libs.bundle("app.ui.debug"))
        }

        packaging {
            resources.excludes.addAll(
                listOf(
                    "/META-INF/{AL2.0,LGPL2.1}",
                    "DebugProbes.kt"
                )
            )

            dex {
                useLegacyPackaging = false
            }
        }
    }
}

internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {
        compileSdk = libs.version("compile.sdk")

        defaultConfig {
            minSdk = libs.version("min.sdk")

            val targetSdkVersion = libs.version("target.sdk")
            if (this is com.android.build.api.dsl.ApplicationDefaultConfig) {
                targetSdk = targetSdkVersion
            } else if (this is com.android.build.api.dsl.LibraryDefaultConfig) {
                testOptions.targetSdk = targetSdkVersion
                lint.targetSdk = targetSdkVersion
            }
        }

        compileOptions {
            sourceCompatibility = javaVersion
            targetCompatibility = javaVersion
        }
    }
}

internal fun Project.configureBuildTypes(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {
        buildTypes {
            create("mock") {
                matchingFallbacks += listOf("debug")
            }
        }
    }
}
