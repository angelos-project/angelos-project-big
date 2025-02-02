import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    id("org.jetbrains.kotlin.multiplatform") version "1.9.25"
    id("com.android.library") version "8.1.0"
    id("com.vanniktech.maven.publish") version "0.29.0"
}

group = "org.angproj.big"
version = "0.9.8"

kotlin {
    explicitApi()
    jvmToolchain(19)

    jvm()
    js {
        browser()
        nodejs()
    }
    // WASM and similar
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        nodejs()
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmWasi { nodejs() }
    // Android
    androidTarget {
        /*compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_1_8)
                }
            }
        }*/
        publishLibraryVariants("release")
    }
    androidNativeArm32()
    androidNativeArm64()
    androidNativeX64()
    androidNativeX86()
    // Linux
    linuxArm64()
    linuxX64()
    // macOS
    macosArm64()
    macosX64()
    // MingW
    mingwX64()
    // iOS
    iosArm64()
    iosX64()
    iosSimulatorArm64()
    // tvOS
    tvosArm64()
    tvosX64()
    tvosSimulatorArm64()
    // watchOS
    watchosArm32()
    watchosArm64()
    watchosDeviceArm64()
    watchosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation("org.angproj.aux:angelos-project-aux:0.9.8")
        }
        jvmTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

android {
    namespace = group.toString()
    compileSdk = 34
    defaultConfig {
        minSdk = 30
    }
    /*compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }*/
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    //signAllPublications()

    coordinates(group.toString(), version.toString())

    pom {
        name.set("My library")
        description.set("A library.")
        inceptionYear.set("2024")
        url.set("https://github.com/kotlin/multiplatform-library-template/")
        licenses {
            license {
                name.set("XXX")
                url.set("YYY")
                distribution.set("ZZZ")
            }
        }
        developers {
            developer {
                id.set("XXX")
                name.set("YYY")
                url.set("ZZZ")
            }
        }
        scm {
            url.set("XXX")
            connection.set("YYY")
            developerConnection.set("ZZZ")
        }
    }
}