plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.ksp)
}

android {
    namespace = "it.unibo.data"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // Project
    implementation(project(":domain"))

    // Room (con KSP)
    implementation(libs.roomRuntime)
    implementation(libs.roomKtx)
    ksp(libs.roomCompiler)

    // Retrofit + Moshi (con KSP)
    implementation(libs.retrofit)
    implementation(libs.retrofitConverterMoshi)
    implementation(libs.moshi)
    implementation(libs.moshiKotlin)
    ksp(libs.moshiCodegen)

    // Android core libs
    implementation(libs.androidxCoreKtx)
    implementation(libs.androidxAppcompat)
    implementation(libs.material)

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidxTestJunit)
    androidTestImplementation(libs.androidxTestEspressoCore)
}