![abc-kmm-notifications: Remote Notification Manager](images/cover.png)

[![Kotlin](https://img.shields.io/badge/kotlin-1.5.21-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![KMM](https://img.shields.io/badge/KMM-0.2.7-lightgreen.svg?logo=KMM)](https://plugins.jetbrains.com/plugin/14936-kotlin-multiplatform-mobile)
[![AGP](https://img.shields.io/badge/AGP-7.0.1-green.svg?logo=AGP)](https://developer.android.com/studio/releases/gradle-plugin)
[![Gradle](https://img.shields.io/badge/Gradle-7.0.2-blue.svg?logo=Gradle)](https://gradle.org)
[![Platform](https://img.shields.io/badge/platform-ios,android-lightgray.svg?style=flat)](https://img.shields.io/badge/platform-ios-lightgray.svg?style=flat)

Remote Notification Manager for [Kotlin Multiplatform Mobile](https://kotlinlang.org/docs/mpp-intro.html)

## Features

- Super easy to use APNs and FCM in one interface
- Dramatically reduce code to write
- Support for generic model mapping
- Support FCM for iOS
- Migration Support for React Native

## Requirements
- iOS
  - Deployment Target 10.0 or higher
- Android
  - minSdkVersion 21

## Installation

### Gradle Settings

Add below gradle settings into your KMP (Kotlin Multiplatform Project)

#### build.gradle.kts in root

```kotlin
buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.21")
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.5.21")
        classpath("com.google.gms:google-services:4.3.5")
    }
}
```
#### build.gradle.kts in shared
```kotlin
plugins {
    id("com.android.library")
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

val abcNotifications = "com.linecorp.abc:kmm-notifications:0.4.1"
val kotlinxSerialization = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2"

kotlin {
    sourceSets {
        ios {
            binaries
                .filterIsInstance<Framework>()
                .forEach {
                    it.transitiveExport = true
                    it.export(abcNotifications)
                }
        }
        android()

        val commonMain by getting {
            dependencies {
                implementation(abcNotifications)
                implementation(kotlinxSerialization)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(abcNotifications)
                api(abcNotifications)
            }
        }
        val iosMain by getting {
            dependencies {
                implementation(abcNotifications)
                api(abcNotifications)
            }
        }
    }
}
```

#### build.gradle.kts in androidApp
```kotlin
plugins {
    id("com.google.gms.google-services")
}
```

## Usage

### Definition of payload and map function with generic in commonMain of your project
```kotlin
@Serializable
data class Data(
  val notificationType: String = "",
  val id: Int = 0,
)

@Throws(Throwable::class)
fun NotificationElement.payload() = decodedPayload<Data>()
```

### Android
```kotlin
ABCNotifications
    .onNewToken(this) {
        // TODO: send to register ${ABCDeviceToken.value} to server
    }
    .onDeletedMessages(this) {
        // TODO: sync messages to server
    }
    .onMessageReceived(this) {
        // FCM RemoteMessage
        val remoteMessage = it.remoteMessage
        
        // decode to Payload with Data
        val payload = it.payload()

        // decode to Data
        val data = it.decodedData<Data>()

        // TODO: present a dialog for push notification
    }
    .beginListening()
```

### iOS

```swift
ABCNotifications.Companion()
    .registerSettings {
        $0.add(type: .alert)
        $0.add(type: .badge)
        $0.add(type: .sound)
    }
    .onNewToken(target: self) {
        // TODO: send to register ${ABCDeviceToken.rawValue} to server
    }
    .onMessageReceived(target: self) {
        guard let payload = try? $0.payload() else { return }

        if $0.isInactive {
            // TODO: present a view controller on inactive
        } else {
            // TODO: present a toast message on active
        }
    }.beginListening()
```

#### Required implementation in AppDelegate

```swift
class AppDelegate: UIResponder, UIApplicationDelegate {

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        ABCNotificationCenterDelegate.Companion().applicationDidFinishLaunching(options: options)
        return true
    }
    
    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        ABCNotificationCenterDelegate.Companion().applicationDidRegisterForRemoteNotifications(deviceToken: deviceToken)
    }
}

extension AppDelegate: UNUserNotificationCenterDelegate {
    
    func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification, withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        ABCNotificationCenterDelegate.Companion().userNotificationCenterWillPresent(notification: notification)
    }
    
    func userNotificationCenter(_ center: UNUserNotificationCenter, didReceive response: UNNotificationResponse, withCompletionHandler completionHandler: @escaping () -> Void) {
        ABCNotificationCenterDelegate.Companion().userNotificationCenterDidReceive(response: response)
    }
}
```

#### FCM on iOS

1. Insert the dependency in shared.podspec
    ```ruby
    spec.dependency 'FirebaseMessaging'
    ```
2. Insert function into cocoapods definition in build.gradle.kts (shared)
    ```kotlin
    cocoapods {
        noPodspec()
    }
    ```

3. Initializing
    ```swift
    ABCNotifications.Companion()
        .allowsFCMOnIOS()
        .registerSettings {
            $0.add(type: .alert)
            $0.add(type: .badge)
            $0.add(type: .sound)
        }
        .onNewToken(target: self) {
            print("onNewToken -> ", ABCDeviceToken.Companion().FCMToken)
            // TODO: send to register ${ABCDeviceToken.FCMToken} to server
        }.beginListening()
    ```

## Advanced

### Shared configuration in commonMain
```kotlin

fun ABCNotifications.Companion.configure(block: ABCNotifications.Companion.() -> Unit) {
    apply(block)

    onNewToken(this) {
        // TODO: send to register ${ABCDeviceToken.value} to server
    }.beginListening()
}
```

### Android
```kotlin

ABCNotifications.configure {
    onDeletedMessages(this) {
        // TODO: sync messages to server
    }
    onMessageReceived(this) {
        // FCM RemoteMessage
        val remoteMessage = it.remoteMessage

        // decode to Payload with Data
        val payload = it.payload()

        // decode to Data
        val data = it.decodedData<Data>()

        // TODO: present a dialog for push notification
    }
}
```

### iOS

```swift
ABCNotifications.Companion().configure { [unowned self] in
    $0.registerSettings {
        $0.add(type: .alert)
        $0.add(type: .badge)
        $0.add(type: .sound)
    }
    $0.onMessageReceived(target: self) {
        guard let payload = try? $0.payload() else { return }

        if $0.isInactive {
            // TODO: present a view controller on inactive
        } else {
            // TODO: present a toast message on active
        }
    }
}
```