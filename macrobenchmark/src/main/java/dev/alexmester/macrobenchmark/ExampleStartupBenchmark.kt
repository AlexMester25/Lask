package dev.alexmester.macrobenchmark

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class BaselineProfileGenerator {

    @get:Rule
    val rule = BaselineProfileRule()

    @Test
    fun generate() = rule.collect(
        packageName = "dev.alexmester.lask",
        includeInStartupProfile = true,
    ) {
        startActivityAndWait {
            it.setPackage("dev.alexmester.lask")
        }

        // Сначала обрабатываем Welcome Screen если он есть
        handleWelcomeScreenIfPresent()

        // Теперь мы точно на главном экране
        interactWithFeed()
        interactWithArticle()
        interactWithBookmarks()
    }

    // ── Welcome Screen ────────────────────────────────────────────────────────

    private fun MacrobenchmarkScope.handleWelcomeScreenIfPresent() {
        // Текст из R.string.welcome_explore — у тебя это кнопка "Explore" или аналог
        // Ждём 3 секунды — если Welcome Screen есть, найдём кнопку
        val hasWelcomeButton = device.wait(
            Until.hasObject(By.text("Explore →")),  // R.string.welcome_explore
            3_000L
        )

        if (hasWelcomeButton) {
            val exploreButton = device.findObject(By.text("Explore →"))
            exploreButton?.click()

            // Ждём пока перейдём на главный экран с лентой
            device.wait(
                Until.hasObject(By.text("Trends")),  // R.string.tab_top_news
                5_000L
            )
        } else {
            // Welcome Screen не показался — onboarding уже пройден
            // Просто ждём главный экран
            device.wait(
                Until.hasObject(By.text("Trends")),
                5_000L
            )
        }
    }

    // ── Лента ─────────────────────────────────────────────────────────────────

    private fun MacrobenchmarkScope.interactWithFeed() {
        val hasFeed = device.wait(
            Until.hasObject(By.scrollable(true)),
            5_000L
        )
        if (!hasFeed) return

        val feed = device.findObject(By.scrollable(true)) ?: return

        repeat(3) {
            feed.scroll(Direction.DOWN, 0.8f)
            Thread.sleep(300)
        }

        feed.scroll(Direction.UP, 1f)
        Thread.sleep(300)
    }

    // ── Статья ────────────────────────────────────────────────────────────────

    private fun MacrobenchmarkScope.interactWithArticle() {
        val hasList = device.wait(
            Until.hasObject(By.scrollable(true)),
            3_000L
        )
        if (!hasList) return

        val firstItem = device.findObject(
            By.clickable(true).hasDescendant(By.scrollable(false))
        ) ?: return

        firstItem.click()

        val articleLoaded = device.wait(
            Until.hasObject(By.scrollable(true)),
            5_000L
        )
        if (!articleLoaded) {
            device.pressBack()
            return
        }

        val articleScroll = device.findObject(By.scrollable(true))

        repeat(2) {
            articleScroll?.scroll(Direction.DOWN, 0.8f)
            Thread.sleep(300)
        }

        device.pressBack()
        Thread.sleep(500)
    }

    // ── Закладки ──────────────────────────────────────────────────────────────

    private fun MacrobenchmarkScope.interactWithBookmarks() {
        val bookmarkTab = device.findObject(By.text("Saved"))
            ?: device.findObject(By.desc("Saved"))
            ?: return

        bookmarkTab.click()
        Thread.sleep(500)

        val feedTab = device.findObject(By.text("Top News"))
            ?: device.findObject(By.desc("Top News"))
            ?: return

        feedTab.click()
        Thread.sleep(300)
    }
}