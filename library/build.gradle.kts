import com.vanniktech.maven.publish.SonatypeHost
import java.net.URI

object This {
    const val longName = "Big Integer implementation - Angelos Project™"
    const val longDescription = "Big Integer Implementation for Angelos Project™ with access to real entropy."
    const val url = "https://github.com/angelos-project/angelos-project-big"
}

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.vanniktech.mavenPublish)
    alias(libs.plugins.dokka)
    alias(libs.plugins.kover)
}

group = "org.angproj.big"
version = "0.10.0"

kotlin {
    explicitApi()
    jvmToolchain(19)

    jvm()
    js {
        browser()
        nodejs()
    }
    // Wasm
    /*wasmJs {
        browser()
        nodejs()
    }
    wasmWasi { nodejs() }*/
    // Android
    androidTarget {
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
            implementation("org.angproj.sec:angelos-project-secrand:0.12.1")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
        }
    }
}

android {
    namespace = group.toString()
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        //sourceCompatibility = JavaVersion.VERSION_11
        //targetCompatibility = JavaVersion.VERSION_11
    }
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    //signAllPublications()

    /**
     * The temporary artifact setup, final is coming later at some point.
     * DO NOT USE FOR SONATYPE NEXUS
     * */
    coordinates(group.toString(), rootProject.name, version.toString())

    pom {
        name.set(This.longName)
        description.set(This.longDescription)
        inceptionYear.set("2023")
        url.set(This.url)

        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/licenses/MIT")
                distribution.set("repo")
            }
        }
        developers {
            developer {
                name.set("Kristoffer Paulsson")
                email.set("kristoffer.paulsson@talenten.se")
                url.set("https://github.com/kristoffer-paulsson")
            }
        }
        scm {
            url.set(This.url)
            connection.set("scm:git:git://github.com/angelos-project/angelos-project-big.git")
            developerConnection.set("scm:git:ssh://github.com:angelos-project/angelos-project-big.git")
        }
    }
}

tasks.dokkaHtml {
    dokkaSourceSets {
        named("commonMain"){
            moduleName.set(This.longName)
            includes.from("Module.md")
            sourceLink {
                localDirectory.set(file("src/commonMain/kotlin"))
                remoteUrl.set(URI(This.url + "/tree/master/library/src/commonMain/kotlin").toURL())
                remoteLineSuffix.set("#L")
            }
        }
    }
}

kover {
    reports {
        total {
            xml.onCheck.set(true)
            html.onCheck.set(true)
        }
    }
}
