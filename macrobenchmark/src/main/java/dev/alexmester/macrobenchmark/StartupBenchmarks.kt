package dev.alexmester.macrobenchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.ExperimentalMacrobenchmarkApi
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class StartupBenchmarks {

    @get:Rule
    val rule = MacrobenchmarkRule()

    /**
     * Холодный старт с установленным baseline-профилем.
     *
     * Профиль ставится заранее с хоста (broadcast `ProfileInstallReceiver` +
     * `cmd package compile -m speed-profile`), поэтому используется [CompilationMode.Ignore]:
     * он не сбрасывает и не перекомпилирует приложение, а измеряет уже подготовленное состояние.
     *
     * Подготовка (APK установлен и хотя бы раз запущен, иначе пакет в состоянии `stopped`):
     * ```
     * adb shell am broadcast -a androidx.profileinstaller.action.INSTALL_PROFILE \
     *     dev.alexmester.lask/androidx.profileinstaller.ProfileInstallReceiver
     * adb shell cmd package compile -f -m speed-profile dev.alexmester.lask
     * ```
     *
     * `CompilationMode.Partial(BaselineProfileMode.Require)` на API 33 без root не подходит:
     * benchmark перед замером делает uninstall+install пакета, после чего пакет оказывается
     * в состоянии `stopped`, и часть OEM-прошивок (Huawei/HarmonyOS) вообще не доставляет
     * broadcast установки профиля — тест падает с
     * "The baseline profile install broadcast was not received".
     */
    @Test
    @OptIn(ExperimentalMacrobenchmarkApi::class)
    fun coldStartupWithBaselineProfile() = rule.measureRepeated(
        packageName = TARGET_PACKAGE,
        metrics = listOf(StartupTimingMetric()),
        iterations = 10,
        startupMode = StartupMode.COLD,
        compilationMode = CompilationMode.Ignore(),
        setupBlock = {
            startActivityAndWait()
            openFeed()
        },
    ) {
        pressHome()
        startActivityAndWait()
    }

    @Test
    fun coldStartupWithoutBaselineProfile() = rule.measureRepeated(
        packageName = TARGET_PACKAGE,
        metrics = listOf(StartupTimingMetric()),
        iterations = 10,
        startupMode = StartupMode.COLD,
        compilationMode = CompilationMode.None(),
        setupBlock = {
            startActivityAndWait()
            openFeed()
        },
    ) {
        pressHome()
        startActivityAndWait()
    }
}
