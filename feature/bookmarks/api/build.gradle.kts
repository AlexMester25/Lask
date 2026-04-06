plugins {
    id("myapp.android.library")
    id("myapp.kotlin.serialization")
}

android {
    namespace = "dev.alexmester.bookmarks.api"
}

dependencies {
    api(project(":core:navigation"))
    api(project(":core:models"))
}