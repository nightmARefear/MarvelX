plugins {
    `kotlin-dsl`
}

group = "com.alexgreece45.marvelx.build_logic"

kotlin {
    jvmToolchain(libs.versions.java.version.get().toInt())
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.compose.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplicationCompose") {
            id = "marvelx.android.application.compose"
            implementationClass = "plugin.AndroidApplicationComposeConventionPlugin"
        }

        register("koinCore") {
            id = "marvelx.koin.core"
            implementationClass = "plugin.KoinConventionPlugin"
        }

        register("androidKoin") {
            id = "marvelx.android.koin"
            implementationClass = "plugin.AndroidKoinConventionPlugin"
        }

        register("androidLibrary") {
            id = "marvelx.android.library"
            implementationClass = "plugin.AndroidLibraryConventionPlugin"
        }

        register("androidLibraryCompose") {
            id = "marvelx.android.library.compose"
            implementationClass = "plugin.AndroidLibraryComposeConventionPlugin"
        }

        register("kotlinBasePlugin") {
            id = "marvelx.kotlin.base.plugin"
            implementationClass = "plugin.KotlinBasePlugin"
        }

        register("kotlinSerializationPlugin") {
            id = "marvelx.kotlin.serialization"
            implementationClass = "plugin.KotlinSerializationPlugin"
        }
    }
}
