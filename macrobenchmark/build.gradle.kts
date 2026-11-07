plugins {
    alias(libs.plugins.android.test)
    alias(libs.plugins.baselineprofile)
}

android {
    namespace = "dev.alexmester.macrobenchmark"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    defaultConfig {
        minSdk = 28
        targetSdk = 36

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // Синтетические build type-ы (nonMinifiedRelease и benchmarkRelease)
    // создаёт плагин androidx.baselineprofile.
    targetProjectPath = ":lask"
}

dependencies {
    implementation(libs.androidx.junit)
    implementation(libs.androidx.espresso.core)
    implementation(libs.androidx.uiautomator)
    implementation(libs.androidx.benchmark.macro)
    implementation(libs.androidx.profileinstaller)
}
