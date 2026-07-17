plugins {
    id("marvelx.android.library")
    id("marvelx.koin.core")
    id("marvelx.kotlin.serialization")
}

android {
    namespace = "com.alexgreece45.marvelx.core.datastore"
}

dependencies {
    api(projects.model)
    implementation(projects.core.common)

    implementation(libs.androidx.datastore.core)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.tink)
    implementation(libs.google.gson)
    implementation(libs.google.tink)
}