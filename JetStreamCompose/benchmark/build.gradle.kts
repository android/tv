import com.android.build.api.dsl.ManagedVirtualDevice

/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    alias(libs.plugins.android.test)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.androidx.baselineprofile)
}

kotlin {
    jvmToolchain(17)
}

android {
    namespace = "com.google.jetstream.benchmark"
    compileSdk = 35

    defaultConfig {
        minSdk = 28
        targetSdk = 35

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["androidx.benchmark.suppressErrors"] = "EMULATOR"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    testOptions.managedDevices.devices {
        create<ManagedVirtualDevice>("tvApi34") {
            device = "Television (1080p)"
            apiLevel = 34
            systemImageSource = "aosp"
        }
    }

    targetProjectPath = ":jetstream"
}

baselineProfile {
    managedDevices += "tvApi34"
    useConnectedDevices = false
}


dependencies {
    implementation(libs.androidx.compose.runtime.base)
    implementation(libs.androidx.junit)
    implementation(libs.androidx.uiautomator)

    // Support for TV activities with LEANBACK_LAUNCHER intent was added in 1.2.0-alpha03 release
    // Use 1.2.0-alpha03 or above versions for benchmarking TV apps
    implementation(libs.androidx.benchmark.macro.junit4)
    implementation(libs.androidx.rules)
}

androidComponents {
    beforeVariants(selector().withBuildType("benchmark").all()) {
        it.enable = true
    }
}
