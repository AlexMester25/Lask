plugins {
    id("myapp.android.library")
    id("myapp.koin")
    id("myapp.datastore")
}

android {
    namespace = "dev.alexmester.datastore"
}

dependencies {
    implementation(project(":core:models"))
    implementation(project(":core:utils"))
    implementation(project(":core:domain"))
}