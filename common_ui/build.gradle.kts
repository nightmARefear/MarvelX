plugins {
    id("marvelx.android.library")
    id("marvelx.koin.core")
}

android {
    namespace = "com.alexgreece45.marvelx.common_ui"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.model)

    implementation(libs.androidx.compat)
}
