import org.jetbrains.kotlin.cli.common.toBooleanLenient

buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        val agpVersion: String by project
        val kotlinVersion: String by project
        classpath("com.android.tools.build:gradle:$agpVersion")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion")
    }
}

allprojects {
    ext {
        set("compileSdk", 30)
        set("minSdk", 21)
        set("targetSdk", 30)
    }

    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven {
            url = uri("http://repo.navercorp.com/maven-release/")
            isAllowInsecureProtocol = true
        }
        maven {
            url = uri("http://repo.navercorp.com/maven-snapshot/")
            isAllowInsecureProtocol = true
        }
    }
}

plugins {
    id("com.android.library")
    id("maven-publish")
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    kotlin("plugin.serialization")
}

val libVersion = "0.4.1"
val firebaseAnalyticsVersion: String by project
val firebaseMessagingVersion: String by project
val kotlinxSerializationVersion: String by project
val sharedStorageVersion: String by project
val isSnapshotUpload = false

group = "com.linecorp.abc"
version = libVersion

kotlin {
    cocoapods {
        summary = "Remote Notification Manager for Kotlin Multiplatform Mobile"
        homepage = "https://git.linecorp.com/abc/abc-kmm-notifications"
        ios.deploymentTarget = "10.0"
        pod("FirebaseMessaging")
    }

    val enableGranularSourceSetsMetadata = project.extra["kotlin.mpp.enableGranularSourceSetsMetadata"]?.toString()?.toBoolean() ?: false
    if (enableGranularSourceSetsMetadata) {
        val iosTarget: (String, org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget.() -> Unit) -> org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget =
            if (System.getenv("SDK_NAME")?.startsWith("iphoneos") == true)
                ::iosArm64
            else
                ::iosX64
        iosTarget("ios") { }
    } else {
        ios()
    }

    android {
        publishAllLibraryVariants()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")
                implementation("com.linecorp.abc:shared-storage:$sharedStorageVersion")
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
                implementation("com.google.firebase:firebase-analytics-ktx:$firebaseAnalyticsVersion")
                implementation("com.google.firebase:firebase-messaging-ktx:$firebaseMessagingVersion")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13")
                implementation("androidx.test:core:1.0.0")
                implementation("androidx.test:runner:1.1.0")
                implementation("org.mockito.kotlin:mockito-kotlin:2.2.10")
                implementation("org.robolectric:robolectric:4.5.1")
                implementation("org.json:json:20210307")
                implementation("androidx.core:core-ktx:1.2.0")
            }
        }
        val iosMain by getting {
            dependencies {
            }
        }
        val iosTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
    }
}

android {
    compileSdk = project.ext.get("compileSdk") as Int

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        minSdk = project.ext.get("minSdk") as Int
        targetSdk = project.ext.get("targetSdk") as Int
    }
    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
        }
    }
    testOptions {
        unitTests.isIncludeAndroidResources = true
        unitTests.isReturnDefaultValues = true
    }
    lint {
        isAbortOnError = false
    }
}

val isMavenLocal = System.getProperty("maven.local").toBooleanLenient() ?: false
if (!isMavenLocal) {
    publishing {
        publications {
            create<MavenPublication>("NaverRepo") {
                if (isSnapshotUpload) {
                    from(components.findByName("debug"))
                } else {
                    from(components.findByName("release"))
                }

                groupId = project.group.toString()
                artifactId = artifactId
                version = if (isSnapshotUpload) "$libVersion-SNAPSHOT" else libVersion

                pom {
                    name.set("$groupId:$artifactId")
                    url.set("https://git.linecorp.com/abc/${project.name}")
                    description.set("Remote Notification Manager for Kotlin Multiplatform")

                    developers {
                        developer {
                            id.set("pisces")
                            name.set("Steve Kim")
                            email.set("pisces@linecorp.com")
                        }
                    }
                    scm {
                        connection.set("scm:git:ssh://git.linecorp.com/abc/${project.name}.git")
                        developerConnection.set("scm:git:ssh://git.linecorp.com/abc/${project.name}.git")
                        url.set("http://git.linecorp.com/abc/${project.name}")
                    }
                }
            }
        }
        repositories {
            maven {
                url = if (isSnapshotUpload) {
                    uri("http://repo.navercorp.com/m2-snapshot-repository")
                } else {
                    uri("http://repo.navercorp.com/maven2")
                }
                isAllowInsecureProtocol = true

                credentials {
                    username = System.getProperty("maven.username") ?: ""
                    password = System.getProperty("maven.password") ?: ""
                }
            }
        }
    }
}