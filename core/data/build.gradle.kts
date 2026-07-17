plugins {
    id("marvelx.android.library")
    id("marvelx.koin.core")
}

android {
    namespace = "com.alexgreece45.marvelx.core.data"
}

dependencies {
    implementation(projects.core.network)
    implementation(projects.core.common)
    implementation(projects.core.datastore)

    implementation(libs.androidx.datastore.core)

    implementation(libs.squareup.retrofit2)
    implementation(libs.google.gson)
}