import dev.alexmester.api.navigation.ArticleDetailApi
import dev.alexmester.api.navigation.BookmarksApi
import dev.alexmester.api.navigation.ExploreApi
import dev.alexmester.api.navigation.InterestsApi
import dev.alexmester.api.navigation.NewsFeedApi
import dev.alexmester.api.navigation.ProfileApi
import dev.alexmester.api.navigation.SearchApi
import dev.alexmester.api.navigation.TypedArticleListApi
import dev.alexmester.impl.di.articleDetail
import dev.alexmester.impl.di.bookmarks
import dev.alexmester.impl.di.explore
import dev.alexmester.impl.di.interests
import dev.alexmester.impl.di.newsFeed
import dev.alexmester.impl.di.profile
import dev.alexmester.impl.di.search
import dev.alexmester.impl.di.typedArticleList
import dev.alexmester.navigation.FeatureApi
import org.junit.Assert.assertEquals
import org.junit.Test
import org.koin.dsl.koinApplication

class FeatureApiBindingsTest {

    @Test
    fun `given feature modules when resolving feature apis then every api has its own implementation`() {
        val app = koinApplication {
            modules(
                articleDetail, bookmarks, explore, interests,
                newsFeed, profile, search, typedArticleList,
            )
        }
        try {
            val koin = app.koin

            val apis: List<FeatureApi> = listOf(
                koin.get<ArticleDetailApi>(),
                koin.get<BookmarksApi>(),
                koin.get<ExploreApi>(),
                koin.get<InterestsApi>(),
                koin.get<NewsFeedApi>(),
                koin.get<ProfileApi>(),
                koin.get<SearchApi>(),
                koin.get<TypedArticleListApi>(),
            )

            assertEquals(8, apis.map { it::class }.toSet().size)
        } finally {
            app.close()
        }
    }
}