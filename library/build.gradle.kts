import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.vanniktech.mavenPublish)
    alias(libs.plugins.dokka)
    jacoco
}

group = "org.angproj.big"
version = "0.9.2"

kotlin {
    explicitApi()
    jvmToolchain(19)

    jvm()
    /*js {
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
    wasmWasi { nodejs() }*/
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
        commonTest.dependencies {
            implementation(libs.kotlin.test)
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

/*tasks.dokkaHtml {
    dokkaSourceSets {
        named("commonMain"){
            moduleName.set("BigInt - Angelos Project™")
            //includes.from("README.md")
            sourceLink {
                localDirectory.set(file("src/commonMain/kotlin"))
                remoteUrl.set(URL("https://github.com/angelos-project/angelos-project-big/tree/master/src/commonMain/kotlin"))
                remoteLineSuffix.set("#L")
            }
        }
    }
}*/

jacoco {
    toolVersion = "0.8.12"
    reportsDirectory.set(layout.buildDirectory.dir("reports/jacoco"))
}

tasks {
    withType<Test> {
        finalizedBy(withType(JacocoReport::class))
    }
    register("jacocoTestReport", JacocoReport::class) {
        dependsOn(withType(Test::class))
        val coverageSourceDirs = arrayOf(
            "src/commonMain",
            "src/jvmMain"
        )

        val buildDirectory = layout.buildDirectory

        val classFiles = buildDirectory.dir("classes/kotlin/jvm").get().asFile
            .walkBottomUp()
            .toSet()

        classDirectories.setFrom(classFiles)
        sourceDirectories.setFrom(files(coverageSourceDirs))

        buildDirectory.files("jacoco/jvmTest.exec").let {
            executionData.setFrom(it)
        }

        reports {
            xml.required.set(true)
            csv.required.set(true)
            html.required.set(true)
        }
    }
}

// https://www.tomaszezula.com/unlocking-test-coverage-in-kotlin-multiplatform-with-jacoco-and-github-actions-part-1/