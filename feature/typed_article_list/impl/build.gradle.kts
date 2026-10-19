plugins {
    id("myapp.android.library")
    id("myapp.android.compose")
    id("myapp.koin")
}

android {
    namespace = "dev.alexmester.impl"
}

dependencies {
    api(project(":feature:typed_article_list:api"))
    implementation(project(":feature:article_detail:api"))

    implementation(project(":core:models"))
    implementation(project(":core:ui"))
    implementation(project(":core:domain"))
}