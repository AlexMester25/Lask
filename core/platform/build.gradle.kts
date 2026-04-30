plugins {
    id("myapp.android.library")
    id("myapp.koin")
}

android {
    namespace = "dev.alexmester.platform"
}

dependencies{
    implementation(project(":core:models"))
    implementation(project(":core:utils"))
    implementation(project(":core:datastore"))
}

