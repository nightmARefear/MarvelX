plugins {
    id("marvelx.android.library")
    id("marvelx.koin.core")
}

android {
    namespace = "com.alexgreece45.marvelx.core.common"
}

dependencies {
    implementation(libs.squareup.logcat)
}
