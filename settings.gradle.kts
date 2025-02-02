pluginManagement {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        gradlePluginPortal()
        //maven(url = "./local-repo")
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }
}

rootProject.name = "angelos-project-big"

/**
 * connectedAndroidTest		    - tests or so
 * connectedDebugAndroidTest	- tests or so
 * deviceAndroidTest			- no tests
 * iosSimulatorArm64Test		- no tests
 * iosX64Test					- done, crash
 * jsBrowserTest				- done, fail
 * jsNodeTest					- done, fail
 * jsTest						- done, fail
 * jvmTest						- no tests
 * linuxX64Test				    - no tests
 * macosArm64Test				- no tests
 * macosX64Test				    - done, crash
 * mingwX64Test				    - no tests
 * testDebugUnitTest			- no tests
 * testReleaseUnitTest			- no tests
 * tvosSimulatorArm64Test		- no tests
 * tvosX64Test					- done, crash
 * wasmJsBrowserTest			- done, fail
 * wasmJsNodeTest				- done, fail
 * wasmJsTest					- done, fail
 * wasmWasiNodeTest			    - done, fail
 * wasmWasiTest				    - done, fail
 * watchosSimulatorArm64Test	- no tests
 * */