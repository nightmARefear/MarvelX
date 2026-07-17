plugins {
    id("marvelx.android.library")
    id("marvelx.koin.core")
}

android {
    namespace = "com.alexgreece45.marvelx.core.network"
}

dependencies {
    implementation(projects.model)
    implementation(projects.core.common)

    implementation(libs.bundles.networking)

    // Chucker
    implementation(libs.chucker)
    "mockImplementation"(libs.chucker)
    releaseImplementation(libs.chucker.no.op)

    implementation(libs.kotlin.serialization)
}