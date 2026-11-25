plugins {
    id("myapp.kotlin.library")
}

dependencies {
    api(project(":core:domain"))
    api(project(":core:models"))

    // MainDispatcherRule расширяет junit TestWatcher и открывает TestDispatcher,
    // поэтому обе зависимости нужны в публичном API тестового модуля.
    api(libs.junit)
    api(libs.kotlinx.coroutines.test)
}
