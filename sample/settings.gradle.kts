pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.namespace == "com.android" || requested.id.name == "kotlin-android-extensions") {
                useModule("com.android.tools.build:gradle:7.0.1")
            }
        }
    }

    val kotlinVersion: String by settings
    plugins {
        kotlin("multiplatform") version "$kotlinVersion"
        kotlin("native.cocoapods") version "$kotlinVersion"
        kotlin("plugin.serialization") version "$kotlinVersion"
    }
}
rootProject.name = "sample"

include(":androidApp")
include(":shared")