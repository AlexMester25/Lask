plugins {
    id("myapp.android.library")
    id("myapp.android.compose")
    id("myapp.koin")
}

android {
    namespace = "dev.alexmester.interests.impl"
}

dependencies {
    api(project(":feature:interests:api"))

    implementation(project(":core:models"))
    implementation(project(":core:ui"))
    implementation(project(":core:datastore"))
    implementation(project(":core:utils"))
    implementation(project(":core:domain"))

}