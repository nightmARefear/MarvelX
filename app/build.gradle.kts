import java.io.FileInputStream
import java.util.Properties

plugins {
    id("marvelx.android.application.compose")
    alias(libs.plugins.google.mapsplatform.secrets)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
//    alias(libs.plugins.kotzilla)
}

val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(rootProject.file("keystore.properties")))

val deeplinkProperties = Properties()
deeplinkProperties.load(FileInputStream(rootProject.file("deeplink.properties")))

android {
    namespace = libs.versions.application.name.get()

    signingConfigs {
        create("release") {
            keyAlias = keystoreProperties["releaseKeyAlias"].toString()
            keyPassword = keystoreProperties["releaseKeyPassword"].toString()
            storeFile = file(keystoreProperties["releaseStoreFile"].toString())
            storePassword = keystoreProperties["releaseStorePassword"].toString()
            enableV1Signing = true
            enableV2Signing = true
        }

        getByName("debug") {
            keyAlias = keystoreProperties["debugKeyAlias"].toString()
            keyPassword = keystoreProperties["debugKeyPassword"].toString()
            storeFile = file(keystoreProperties["debugStoreFile"].toString())
            storePassword = keystoreProperties["debugStorePassword"].toString()
        }
    }

    defaultConfig {
        applicationId = libs.versions.application.name.get()
        minSdk = libs.versions.min.sdk.get().toInt()
        targetSdk = libs.versions.target.sdk.get().toInt()
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

            signingConfig = signingConfigs.getByName("debug")
        }

        getByName("mock") {
            applicationIdSuffix = ".mock"
            isMinifyEnabled = false
            enableUnitTestCoverage = true
            isDebuggable = true

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

            signingConfig = signingConfigs.getByName("debug")

            matchingFallbacks += listOf("debug")
        }
    }

    // TODO: To be transferred to kotlin block (kotlin {}).
//    kotlinOptions {
//        jvmTarget = JavaVersion.toVersion(libs.versions.java.version.get()).toString()
//    }

//    kotlin {
//        compilerOptions {
//            freeCompilerArgs.add("-XXLanguage:+PropertyParamAnnotationDefaultTargetMode")
//
//            // Compose metrics.
//            freeCompilerArgs.addAll(
//                "-P",
//                "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=" +
//                        project.buildDir.absolutePath + "/compose_metrics"
//            )
//            freeCompilerArgs.addAll(
//                "-P",
//                "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=" +
//                        project.buildDir.absolutePath + "/compose_metrics"
//            )
//
//            // Explicit backing fields.
//            freeCompilerArgs.add("-XXLanguage:+ExplicitBackingFields")
//        }
//    }
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
    // Other modules.
    implementation(projects.commonUi)
    implementation(projects.core.common)
    implementation(projects.core.network)
    implementation(projects.core.datastore)
    implementation(projects.core.data)
    implementation(projects.core.security)
    implementation(projects.model)

    // Kotlin reflection.
    implementation(libs.kotlin.reflect)

    // Kotlin immutable lists.
    implementation(libs.kotlin.collections.immutable)

    // Compose.
    implementation(project.dependencies.platform(libs.androidx.compose.bom))
//    implementation(libs.bundles.app.ui)
    implementation(libs.bundles.app.ui.camera)
    implementation(libs.androidx.compose.ui.tooling) // Added to fix render issue (ClassNotFoundException: ComposeViewAdapter)
    debugImplementation(libs.bundles.app.ui.debug)
    "mockImplementation"(libs.bundles.app.ui.debug)

    // QR code.
    implementation(libs.google.zxing)

    // Testing.
    testImplementation(libs.bundles.test.implementation)
    androidTestImplementation(libs.bundles.android.test.implementation)

    implementation(libs.bundles.networking)

    // Chucker
    debugImplementation(libs.chucker)
    releaseImplementation(libs.chucker.no.op)
    "mockImplementation"(libs.chucker)

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
    implementation(libs.koin.androidx.work.manager)
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
    implementation(libs.google.firebase.cloud.messaging)

    // Image cropper.
    implementation(libs.vanniktech.android.image.cropper)

    // Reorderable composables.
    implementation(libs.sh.calvin.reorderable)

    // Stepper.
    implementation(libs.binayshaw.kotstep)

    implementation(libs.androidx.datastore.preferences)

//    implementation(libs.kotzilla.sdk.compose)
}