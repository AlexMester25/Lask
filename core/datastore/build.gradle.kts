plugins {
    id("myapp.android.library")
    id("myapp.koin")
    id("myapp.kotlin.serialization")
    id("myapp.datastore")
}

android {
    namespace = "dev.alexmester.datastore"

}

dependencies {
    implementation(project(":core:models"))
}