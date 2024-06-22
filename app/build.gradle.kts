import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.mapsplatform.secrets)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
}

val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(rootProject.file("keystore.properties")))

val deeplinkProperties = Properties()
deeplinkProperties.load(FileInputStream(rootProject.file("deeplink.properties")))

android {
    namespace = libs.versions.application.name.get()
    compileSdk = 34

    signingConfigs {
        create("release") {
            keyAlias = keystoreProperties["releaseKeyAlias"].toString()
            keyPassword = keystoreProperties["releaseKeyPassword"].toString()
            storeFile = file(keystoreProperties["releaseStoreFile"].toString())
            storePassword = keystoreProperties["releaseStorePassword"].toString()
            enableV1Signing = true
            enableV2Signing = true
        }
    }

    defaultConfig {
        applicationId = libs.versions.application.name.get()
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
            enableAndroidTestCoverage = false
            enableUnitTestCoverage = false
            isJniDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true

            manifestPlaceholders.putAll(
                mapOf(
                    "appNameSuffix" to "",
                    "deeplinkScheme" to deeplinkProperties["deeplinkScheme"].toString(),
                    "deeplinkHost" to deeplinkProperties["releaseHost"].toString(),
                    "deeplinkPort" to deeplinkProperties["releasePort"].toString(),
                    "deeplinkPathAdvancedPattern" to deeplinkProperties["deeplinkPathAdvancedPattern.regexp"].toString(),
                )
            )

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

            manifestPlaceholders.putAll(
                mapOf(
                    "appNameSuffix" to "[DEBUG]",
                    "deeplinkScheme" to deeplinkProperties["deeplinkScheme"].toString(),
                    "deeplinkHost" to deeplinkProperties["debugHost"].toString(),
                    "deeplinkPort" to deeplinkProperties["debugPort"].toString(),
                    "deeplinkPathAdvancedPattern" to deeplinkProperties["deeplinkPathAdvancedPattern.regexp"].toString(),
                )
            )

            versionNameSuffix = "-DEBUG"
        }

        create("mock") {
            applicationIdSuffix = ".mock"
            isMinifyEnabled = false
            enableUnitTestCoverage = true

            manifestPlaceholders.putAll(
                mapOf(
                    "appNameSuffix" to "[MOCK]",
                    "deeplinkScheme" to deeplinkProperties["deeplinkScheme"].toString(),
                    "deeplinkHost" to deeplinkProperties["debugHost"].toString(),
                    "deeplinkPort" to deeplinkProperties["debugPort"].toString(),
                    "deeplinkPathAdvancedPattern" to deeplinkProperties["deeplinkPathAdvancedPattern.regexp"].toString(),
                )
            )

            versionNameSuffix = "-MOCK"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.java.version.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.java.version.get())
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    kotlinOptions {
        jvmTarget = JavaVersion.toVersion(libs.versions.java.version.get()).toString()
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }

    packaging {
        resources.excludes.addAll(
            listOf(
                "/META-INF/{AL2.0,LGPL2.1}",
                "DebugProbes.kt"
            )
        )

        dex {
            useLegacyPackaging = false
        }
    }

    sourceSets.all {
        kotlin.srcDir("build/generated/ksp/$name/kotlin")
    }
}

// TODO: TO BE FIXED

/*// Enforce the "updateDexCrcInStringsXmlTask" task to run before obfuscation and minification.
tasks.whenTaskAdded {
    if (this.name == "minifyReleaseWithR8" || this.name == "minifyReleaseWithProguard") {
        this.dependsOn("updateDexCrcInStringsXmlTask")
    }
}

// Ensure the task runs for debug builds
tasks.whenTaskAdded {
    if (this.name == "assembleDebug") {
        this.finalizedBy("updateDexCrcInStringsXmlTask")
    }
}

// Build debug APK
tasks.register("buildDebugApk") {
    dependsOn("assembleDebug")
}*/

dependencies {
    // Kotlin reflection.
    implementation(libs.kotlin.reflect)

    // Compose.
    implementation(project.dependencies.platform(libs.androidx.compose.bom))
    implementation(libs.bundles.app.ui)
    debugImplementation(libs.bundles.app.ui.debug)

    // QR code.
    implementation(libs.google.zxing)

    // Testing.
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
    implementation(libs.bundles.koin.implementation)
    ksp(libs.koin.ksp.compiler)
    testImplementation(libs.bundles.koin.test.implementation)

    // Bouncycastle. Contains Blake2 algorithm.
    implementation(libs.bouncycastle.bcprov)

    // Firebase.
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(platform(libs.google.firebase.bom))
    implementation(libs.google.android.libraries.identity)
    implementation(libs.google.firebase.analytics)
    implementation(libs.google.firebase.crashlytics)
    implementation(libs.google.play.services.auth)
    implementation(libs.google.firebase.auth)
    implementation(libs.google.firebase.firestore)

    // Shimmer.
    implementation(libs.valentinilk.shimmer)
}