plugins {
    id("myapp.android.library")
    id("myapp.android.compose")
    id("myapp.koin")
}

android {
    namespace = "dev.alexmester.settings.impl"
}

dependencies {
    api(project(":feature:settings:api"))

    implementation(project(":core:models"))
    implementation(project(":core:ui"))
    implementation(project(":core:utils"))
    implementation(project(":core:domain"))

    testImplementation(project(":core:testing"))
}
