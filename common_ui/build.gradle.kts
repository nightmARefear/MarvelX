plugins {
    id("marvelx.android.library")
    id("marvelx.koin.core")
    id("marvelx.android.library.compose")
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "com.alexgreece45.marvelx.common_ui"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.model)
    api(projects.uiTheme)

    implementation(libs.androidx.compat)
}