
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
        testInstrumentationRunner ="androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary= true
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
            proguardFiles (getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }

        getByName("debug") {
            applicationIdSuffix =".debug"
            debuggable= true
            minifyEnabled =false

            manifestPlaceholders["appNameSuffix"] = "[DEBUG]"
            versionNameSuffix ="-DEBUG"
//            signingConfig signingConfigs.debug // If uncommented, the respective .apk files are generated.
        }

        getByName("mock") {
            applicationIdSuffix= ".mock"
            debuggable =true
            minifyEnabled= false

            manifestPlaceholders["appNameSuffix"] = "[MOCK]"
            versionNameSuffix= "-MOCK"
//            signingConfig signingConfigs.debug // If uncommented, the respective .apk files are generated.
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }

    packaging {
        resources.excludes.add(
                "/META-INF/{AL2.0,LGPL2.1}"
        )
    }

    kapt {
        correctErrorTypes = true
    }
}

dependencies {
    implementation(libs.bundles.app.ui.release)
    debugImplementation(libs.bundles.app.ui.debug)

    testImplementation(libs.bundles.test-implementation)
    androidTestImplementation(libs.bundles.android.test.implementation)

    implementation(libs.bundles.networking)

    // Chucker
    debugImplementantion(libs.libraries.chucker)
    releaseImplementantion(libs.libraries.chucker.no.op)

    // Dagger - Hilt
    implementation(libs.libraries.androidx.hilt.work)
    implementation(libs.libraries.google.dagger.hilt)
    kapt(libs.libraries.google.dagger.hilt.compiler)
    ksp(libs.libraries.androidx.hilt.compiler)
    testImplementation(libs.libraries.google.dagger.hilt.android.testing)
    kaptTest(libs.libraries.google.dagger.hilt.compiler)

    implementation(libs.libraries.squareup.logcat)
    implementation(libs.bundles.coil)

    // Database
    implementation(libs.libraries.androidx.room.runtime)
    implementation(libs.libraries.androidx.room.ktx)
    annotationProcessor(libs.libraries.androidx.room.compiler)

    ksp(libs.libraries.androidx.room.ktx)

    // optional - Test helpers
    testImplementation(libs.libraries.androidx.room.testing)

    // optional - Paging 3 Integration
    implementation(libs.libraries.androidx.room.paging)

    implementation(libs.bundles.security)

    implementation(libs.libraries.airbnb.lottie.compose)

    debugImplementation(libs.libraries.squareup.leakcanary)

    // Work Manager - Kotlin + coroutines & Test helpers
    implementation(libs.libraries.androidx.work.runtime.ktx)
    androidTestImplementation(libs.libraries.androidx.work.testing)

    implementation(libs.libraries.rootbeer)
}