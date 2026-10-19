plugins {
    id("myapp.android.library")
    id("myapp.android.compose")
    id("myapp.koin")
}

android {
    namespace = "dev.alexmester.news_feed.impl"
}

dependencies {
    api(project(":feature:news_feed:api"))
    implementation(project(":feature:article_detail:api"))

    implementation(project(":core:models"))
    implementation(project(":core:ui"))
    implementation(project(":core:utils"))
    implementation(project(":core:platform"))
    implementation(project(":core:domain"))
}