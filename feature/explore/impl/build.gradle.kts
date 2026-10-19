plugins {
    id("myapp.android.library")
    id("myapp.android.compose")
    id("myapp.koin")
}

android {
    namespace = "dev.alexmester.explore.impl"
}

dependencies {
    api(project(":feature:explore:api"))
    implementation(project(":feature:article_detail:api"))
    implementation(project(":feature:search:api"))

    implementation(project(":core:models"))
    implementation(project(":core:ui"))
    implementation(project(":core:utils"))
    implementation(project(":core:platform"))
    implementation(project(":core:domain"))

}