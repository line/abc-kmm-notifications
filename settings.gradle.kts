pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.namespace == "com.android" || requested.id.name == "kotlin-android-extensions") {
                val agpVersion: String by settings
                useModule("com.android.tools.build:gradle:$agpVersion")
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

rootProject.name = "kmm-notifications"