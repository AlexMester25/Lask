package dev.alexmester.macrobenchmark

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until

internal const val TARGET_PACKAGE = "dev.alexmester.lask"

/**
 * Test-теги из Compose-исходников приложения. На экране они публикуются как
 * resource-id благодаря `Modifier.semantics { testTagsAsResourceId = true }`.
 */
internal object TestTags {
    const val WELCOME_START = "welcome_start"
    const val FEED_LIST = "feed_list"
    const val ARTICLE_CARD = "article_card"
    const val ARTICLE_SCROLL = "article_detail_scroll"
}

/** Закрывает onboarding, если приложение запущено впервые. */
internal fun MacrobenchmarkScope.dismissOnboardingIfPresent(timeoutMs: Long = 2_000) {
    val startButton = device.wait(
        Until.findObject(By.res(TestTags.WELCOME_START)),
        timeoutMs,
    ) ?: return
    startButton.click()
    device.wait(Until.gone(By.res(TestTags.WELCOME_START)), 5_000)
    device.waitForIdle()
}

/** Ждёт, пока лента новостей отрисует список статей. */
internal fun MacrobenchmarkScope.waitForFeed(timeoutMs: Long = 15_000) {
    device.wait(Until.hasObject(By.res(TestTags.FEED_LIST)), timeoutMs)
    device.waitForIdle()
}

/** Приводит приложение к главному экрану с лентой. */
internal fun MacrobenchmarkScope.openFeed() {
    dismissOnboardingIfPresent()
    waitForFeed()
}

/** Прокручивает ленту вниз и возвращается наверх, чтобы итерации были одинаковыми. */
internal fun MacrobenchmarkScope.scrollFeed(flings: Int = 3) {
    val feed = device.findObject(By.res(TestTags.FEED_LIST)) ?: return
    feed.setGestureMargin(device.displayWidth / 5)
    repeat(flings) {
        feed.fling(Direction.DOWN)
        device.waitForIdle()
    }
    feed.fling(Direction.UP)
    device.waitForIdle()
}

/** Открывает первую статью, прокручивает её и возвращается назад. */
internal fun MacrobenchmarkScope.openFirstArticleAndScroll() {
    val card = device.findObject(By.res(TestTags.ARTICLE_CARD)) ?: return
    card.click()

    val article = device.wait(
        Until.findObject(By.res(TestTags.ARTICLE_SCROLL)),
        5_000,
    ) ?: run {
        device.pressBack()
        return
    }

    article.setGestureMargin(device.displayWidth / 5)
    repeat(2) {
        article.fling(Direction.DOWN)
        device.waitForIdle()
    }

    device.pressBack()
    device.waitForIdle()
}
