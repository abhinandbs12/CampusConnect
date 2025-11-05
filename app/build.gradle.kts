plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.vineet.campusconnect"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.vineet.campusconnect"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.firebase.auth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // This is for the Firebase "Bill of Materials" (BOM)
    // It helps manage all Firebase library versions together
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))


// This is the actual library for the Firestore Database
    implementation("com.google.firebase:firebase-firestore")
    // This adds the Material Design components library
    implementation("com.google.android.material:material:1.12.0")


}