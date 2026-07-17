plugins {
    id("marvelx.android.library")
    id("marvelx.koin.core")
}

android {
    namespace = "com.alexgreece45.marvelx.core.security"
}

dependencies {
    implementation(projects.core.common)

    implementation(libs.rootbeer)
    implementation(libs.bouncycastle.bcprov)
}