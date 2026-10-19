plugins {
    id("myapp.android.library")
    id("myapp.koin")
    id("myapp.ktor")
    id("myapp.kotlin.serialization")
    id("myapp.datastore")
}

android {
    namespace = "dev.alexmester.data"
}

dependencies {
    implementation(project(":core:models"))
    implementation(project(":core:domain"))
    implementation(project(":core:network"))
    implementation(project(":core:utils"))
    implementation(project(":core:datastore"))
    implementation(project(":core:database"))
}