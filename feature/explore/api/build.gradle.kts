plugins {
    id("myapp.android.library")
    id("myapp.kotlin.serialization")
}

android {
    namespace = "dev.alexmester.explore.api"
}

dependencies {
    api(project(":core:navigation"))
}