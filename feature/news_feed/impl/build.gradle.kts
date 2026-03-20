plugins {
    id("myapp.android.library")
    id("myapp.android.compose")
    id("myapp.koin")
    id("myapp.kotlin.serialization")
    id("myapp.room")
    id("myapp.ktor")
}

android {
    namespace = "dev.alexmester.newsfeed.impl"
}

dependencies {
    api(project(":feature:news_feed:api"))

    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:models"))
    implementation(project(":core:ui"))
    implementation(project(":core:datastore"))
}