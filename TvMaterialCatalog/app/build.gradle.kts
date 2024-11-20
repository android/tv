@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
}

kotlin {
    jvmToolchain(17)
}

android {
    namespace = "com.google.tv.material.catalog"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.google.tv.material.catalog"
        minSdk = 21
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        vectorDrawables {
            useSupportLibrary = true
        }

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(platform(libs.compose.bom))
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(libs.tv.foundation)
    implementation(libs.tv.material)
    implementation(libs.coil.compose)
    implementation(libs.navigation.compose)
    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)
}