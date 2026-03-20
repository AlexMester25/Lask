plugins {
    id("myapp.android.library")
    id("myapp.android.compose")
    id("myapp.kotlin.serialization")
}

android {
    namespace = "dev.alexmester.navigation"
}

dependencies {
    api(libs.androidx.navigation.compose)
    api(libs.kotlinx.serialization.json)
}