plugins {
    id("myapp.android.library")
    id("myapp.room")
    id("myapp.koin")
}

android {
    namespace = "dev.alexmester.database"
}

dependencies {
    api(libs.bundles.room)
    api(libs.bundles.koin)
    api(libs.kotlinx.coroutines.core)
}