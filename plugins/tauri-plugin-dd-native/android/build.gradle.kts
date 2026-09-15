plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// `compileOnly(project(":tauri-android"))` below refers to the Tauri
// runtime module that `tauri android init` wires into settings.gradle.kts
// automatically when it discovers this plugin (via the path dependency in
// ../../src-tauri/Cargo.toml) — nothing to configure by hand here. If a
// future Tauri CLI version ever renames that module, this is the one line
// to update; see README "If the Android build fails" for how to spot it.
android {
    namespace = "com.darshan.compressor.nativebridge"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
}

dependencies {
    compileOnly(project(":tauri-android"))

    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.webkit:webkit:1.12.1")
    implementation("androidx.lifecycle:lifecycle-process:2.8.7")
    implementation("androidx.appcompat:appcompat:1.7.0")

    // The ad system this plugin implements — see AdUnitIds.kt and README
    // "Ad strategy" for the policy these are used under (banner + app-open
    // only, never interstitial/rewarded/video).
    implementation("com.google.android.gms:play-services-ads:24.9.0")
    implementation("com.google.android.ump:user-messaging-platform:3.2.0")
}
