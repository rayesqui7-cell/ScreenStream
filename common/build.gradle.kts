plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.compose)
}

kotlin {
    explicitApi()
    jvmToolchain(17)
}

android {
    namespace = "info.dvkr.screenstream.common"
    compileSdk = rootProject.extra["compileSdkVersion"] as Int
    buildToolsVersion = rootProject.extra["buildToolsVersion"] as String

    defaultConfig {
        minSdk = rootProject.extra["minSdkVersion"] as Int
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    api(libs.kotlinStdlibJdk8)
    api(libs.kotlinReflect)
    api(libs.kotlinx.coroutines.android)

    api(libs.androidx.core.ktx)
    api(libs.androidx.activity.compose)
    api(libs.androidx.fragment)
    api(libs.androidx.appcompat)
    api(libs.androidx.lifecycle.runtime.compose)
    api(libs.androidx.window)
    api(libs.androidx.datastore.preferences)

    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.material.icons.core)
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.material3.window)

    api(libs.koin.android.compose)

    api(libs.xlog)
}