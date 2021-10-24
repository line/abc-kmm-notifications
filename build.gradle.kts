
buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        mavenLocal()
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
        mavenLocal()
    }
}

plugins {
    id("com.android.library")
    id("maven-publish")
    id("signing")
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    kotlin("plugin.serialization")
}

val firebaseAnalyticsVersion: String by project
val firebaseMessagingVersion: String by project
val kotlinxSerializationVersion: String by project
val sharedStorageVersion: String by project
val isSnapshotUpload = false

group = "com.linecorp.abc"
version = "0.4.1"

kotlin {
    cocoapods {
        summary = "Remote Notification Manager for Kotlin Multiplatform Mobile"
        homepage = "https://github.com/line/abc-kmm-notifications"
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
                implementation("com.linecorp.abc:kmm-shared-storage:$sharedStorageVersion")
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
        val iosMain by getting
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

publishing {
    publications {
        create<MavenPublication>("abcNotifications") {
            if (isSnapshotUpload) {
                from(components.findByName("debug"))
            } else {
                from(components.findByName("release"))
            }

            groupId = project.group.toString()
            artifactId = project.name
            version = if (isSnapshotUpload) "${rootProject.version}-SNAPSHOT" else rootProject.version.toString()
            val gitRepositoryName = "abc-$artifactId"

            pom {
                name.set(artifactId)
                description.set("Remote Notification Manager for Kotlin Multiplatform Mobile iOS and android")
                url.set("https://github.com/line/$gitRepositoryName")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        name.set("LINE Corporation")
                        email.set("dl_oss_dev@linecorp.com")
                        url.set("https://engineering.linecorp.com/en/")
                    }
                    developer {
                        id.set("pisces")
                        name.set("Steve Kim")
                        email.set("pisces@linecorp.com")
                    }
                }

                scm {
                    connection.set("scm:git@github.com:line/$gitRepositoryName.git")
                    developerConnection.set("scm:git:ssh://github.com:line/$gitRepositoryName.git")
                    url.set("http://github.com/line/$gitRepositoryName")
                }
            }
        }
    }
    repositories {
        maven {
            name = "MavenCentral"
            url = if (isSnapshotUpload) {
                uri("https://oss.sonatype.org/content/repositories/snapshots/")
            } else {
                uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            }

            val sonatypeUsername: String? by project
            val sonatypePassword: String? by project

            println("sonatypeUsername, sonatypePassword -> $sonatypeUsername, ${sonatypePassword?.masked()}")

            credentials {
                username = sonatypeUsername ?: ""
                password = sonatypePassword ?: ""
            }
        }
    }
}

signing {
    val signingKey: String? by project
    val signingPassword: String? by project

    println("signingKey, signingPassword -> ${signingKey?.slice(0..9)}, ${signingPassword?.masked()}")

    isRequired = !isSnapshotUpload
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications["abcNotifications"])
}

fun String.masked() = map { "*" }.joinToString("")