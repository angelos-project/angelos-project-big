plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
}

group = "org.angproj.big"
version = "1.0-SNAPSHOT"

kotlin {
    explicitApi()
    jvmToolchain(libs.versions.jvm.toolchain.get().toInt())

    jvm()
    androidTarget {
        publishLibraryVariants("release")
    }

    sourceSets {
        jvmMain.dependencies {
            api(project(":library"))
            implementation(libs.kotlin.test)
            implementation("com.code-intelligence:jazzer:0.24.0")
            implementation("com.code-intelligence:jazzer-api:0.24.0")
            //implementation(libs.kotlin.jazzer)
            //implementation(libs.kotlin.jazzer.api)
        }
    }
}

android {
    namespace = group.toString()
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    /*compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }*/
}