import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.mapsplatform.secrets)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.parcelize)
}

// Load keystore credentials.
val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(rootProject.file("keystore.properties")))

android {
    namespace = "com.alexgreece45.marvelx"
    compileSdk = 34

    signingConfigs {
        create("release") {
            keyAlias = keystoreProperties["keyAlias"].toString()
            keyPassword = keystoreProperties["keyPassword"].toString()
            storeFile = file(keystoreProperties["storeFile"].toString())
            storePassword = keystoreProperties["storePassword"].toString()
            enableV1Signing = true
            enableV2Signing = true
        }
    }

    defaultConfig {
        applicationId = "com.alexgreece45.marvelx"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        getByName("release") {
            enableUnitTestCoverage = false
            enableAndroidTestCoverage = false
            isJniDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true

            manifestPlaceholders["appNameSuffix"] = ""
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }

        getByName("debug") {
            applicationIdSuffix = ".debug"
            isMinifyEnabled = false
            enableUnitTestCoverage = true

            manifestPlaceholders["appNameSuffix"] = "[DEBUG]"
            versionNameSuffix = "-DEBUG"
        }

        create("mock") {
            applicationIdSuffix = ".mock"
            isMinifyEnabled = false
            enableUnitTestCoverage = true

            manifestPlaceholders["appNameSuffix"] = "[MOCK]"
            versionNameSuffix = "-MOCK"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }

    packaging {
        resources.excludes.add(
            "/META-INF/{AL2.0,LGPL2.1}"
        )

        dex {
            useLegacyPackaging = false
        }
    }
}

dependencies {
    implementation(libs.kotlin.reflect)

    implementation(libs.bundles.app.ui.release)
    implementation(project.dependencies.platform(libs.androidx.compose.bom))
    debugImplementation(libs.bundles.app.ui.debug)

    testImplementation(libs.bundles.test.implementation)
    androidTestImplementation(libs.bundles.android.test.implementation)

    implementation(libs.bundles.networking)

    // Chucker
    debugImplementation(libs.chucker)
    releaseImplementation(libs.chucker.no.op)

    implementation(libs.squareup.logcat)
    implementation(libs.bundles.coil)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    annotationProcessor(libs.androidx.room.compiler)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.paging)

    implementation(libs.bundles.security)

    implementation(libs.airbnb.lottie.compose)

    debugImplementation(libs.squareup.leakcanary)

    // Work Manager - Kotlin + coroutines & Test helpers
    implementation(libs.androidx.work.runtime.ktx)
    androidTestImplementation(libs.androidx.work.testing)

    implementation(libs.rootbeer)

    // Koin.
    implementation(platform(libs.koin.bom))
    implementation(libs.bundles.koin)
    testImplementation(libs.bundles.koin.test.implementation)
}