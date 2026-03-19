plugins {
    id("myapp.android.library")
    id("myapp.ktor")
    id("myapp.koin")
    id("myapp.kotlin.serialization")
}

android {
    namespace = "dev.alexmester.network"

}

dependencies {
    implementation(project(":core:models"))

    api(libs.bundles.ktor)
    api(libs.kotlinx.serialization.json)
    api(libs.bundles.koin)
    api(libs.kotlinx.coroutines.core)
}