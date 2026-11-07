package dev.alexmester.macrobenchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.ExperimentalMacrobenchmarkApi
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class FeedScrollBenchmarks {

    @get:Rule
    val rule = MacrobenchmarkRule()

    /**
     * Скролл ленты с установленным baseline-профилем.
     *
     * Состояние ART готовится с хоста, см. `StartupBenchmarks#coldStartupWithBaselineProfile`,
     * поэтому используется [CompilationMode.Ignore].
     */
    @Test
    @OptIn(ExperimentalMacrobenchmarkApi::class)
    fun feedScrollWithBaselineProfile() = rule.measureRepeated(
        packageName = TARGET_PACKAGE,
        metrics = listOf(FrameTimingMetric()),
        iterations = 5,
        compilationMode = CompilationMode.Ignore(),
        setupBlock = {
            startActivityAndWait()
            openFeed()
        },
    ) {
        scrollFeed()
    }

    @Test
    fun feedScrollWithoutBaselineProfile() = rule.measureRepeated(
        packageName = TARGET_PACKAGE,
        metrics = listOf(FrameTimingMetric()),
        iterations = 5,
        compilationMode = CompilationMode.None(),
        setupBlock = {
            startActivityAndWait()
            openFeed()
        },
    ) {
        scrollFeed()
    }
}
