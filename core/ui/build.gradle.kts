plugins {
    id("myapp.android.library")
    id("myapp.android.compose")
}

android {
    namespace = "dev.alexmester.ui"
}

dependencies{
    api("com.google.accompanist:accompanist-systemuicontroller:0.34.0")
}

