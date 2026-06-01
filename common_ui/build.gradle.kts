plugins {
    id("marvelx.android.library")
    id("marvelx.koin.core")
}

android {
    namespace = "com.alexgreece45.marvelx.common_ui"
}

dependencies {
    implementation(project(":core:common"))

    implementation(libs.androidx.compat)
}
