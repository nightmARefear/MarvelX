plugins {
    id("marvelx.android.library")
    id("marvelx.koin.core")
    id("marvelx.android.library.compose")
}

android {
    namespace = "com.alexgreece45.marvelx.ui_theme"
}

dependencies {
    api(libs.valentinilk.shimmer)
}