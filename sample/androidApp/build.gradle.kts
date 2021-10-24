plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    kotlin("android")
}

dependencies {
    implementation(project(":shared"))
    implementation("com.google.android.material:material:1.3.0")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
}

android {
    compileSdk = 30
    defaultConfig {
        applicationId = "com.linecorp.abc.notifications.Sample"
        minSdk = 21
        targetSdk = 30
    }
}