plugins {
    id("myapp.android.library")
    id("myapp.android.compose")
    id("myapp.koin")
}

android {
    namespace = "dev.alexmester.bookmarks.impl"
}

dependencies {
    api(project(":feature:bookmarks:api"))
    implementation(project(":feature:article_detail:api"))

    implementation(project(":core:database"))
    implementation(project(":core:models"))
    implementation(project(":core:ui"))
    implementation(project(":core:platform"))
}