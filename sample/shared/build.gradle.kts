import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id("com.android.library")
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    kotlin("plugin.serialization")
}

val abcNotificationsVersion: String by project
val abcNotifications = "com.linecorp.abc:kmm-notifications:$abcNotificationsVersion"
val kotlinxSerializationVersion: String by project
version = "1.0"

kotlin {
    val iosTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget =
        if (System.getenv("SDK_NAME")?.startsWith("iphoneos") == true)
            ::iosArm64
        else
            ::iosX64
    iosTarget("ios") {
        binaries
            .filterIsInstance<Framework>()
            .forEach {
                it.transitiveExport = true
                it.export(abcNotifications)
            }
    }

    android()

    cocoapods {
        ios.deploymentTarget = "10.0"
        homepage = "https://github.com/line/abc-kmm-notifications/sample/iosApp"
        summary = "Sample for abc-kmm-notifications"
        podfile = project.file("../iosApp/Podfile")
        noPodspec()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(abcNotifications)
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(abcNotifications)
                api(abcNotifications)
                api("ch.qos.logback:logback-classic:1.2.3")
                api("com.google.firebase:firebase-analytics-ktx:18.0.3")
                api("com.google.firebase:firebase-messaging-ktx:21.0.1")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13.2")
                implementation("androidx.test:core:1.0.0")
                implementation("androidx.test:runner:1.1.0")
                implementation("org.robolectric:robolectric:4.2")
            }
        }
        val iosMain by getting {
            dependencies {
                implementation(abcNotifications)
                api(abcNotifications)
            }
        }
        val iosTest by getting
    }
}

android {
    compileSdk = 30
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDir("src/androidMain/res")
    defaultConfig {
        minSdk = 21
        targetSdk = 30
    }
    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
        }
    }
    lint {
        isAbortOnError = false
    }
}