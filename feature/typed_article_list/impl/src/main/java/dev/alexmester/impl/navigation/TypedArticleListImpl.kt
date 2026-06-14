package dev.alexmester.impl.navigation

import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import dev.alexmester.api.navigation.ArticleDetailApi
import dev.alexmester.api.navigation.ArticleListType
import dev.alexmester.api.navigation.TypedArticleListApi
import dev.alexmester.api.navigation.TypedArticleListRoute
import dev.alexmester.impl.presentation.article_list.TypedArticleListScreen
import dev.alexmester.ui.shared_transition.SharedTransitionLocals

class TypedArticleListImpl(
    private val articleDetailApi: ArticleDetailApi,
): TypedArticleListApi {

    override fun typedArticleListRoute(type: ArticleListType) =
        TypedArticleListRoute(type)

    override fun registerGraph(
        navGraphBuilder: NavGraphBuilder,
        navController: NavHostController
    ) {
        navGraphBuilder.composable<TypedArticleListRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<TypedArticleListRoute>()
            CompositionLocalProvider(
                SharedTransitionLocals.LocalAnimatedVisibilityScope provides this,
            ) {
                TypedArticleListScreen(
                    type = route.type,
                    onBack = { navController.navigateUp() },
                    onArticleClick = { id, url ->
                        navController.navigate(
                            articleDetailApi.articleDetailRoute(
                                articleId = id,
                                articleUrl = url,
                            )
                        )
                    },
                )
            }
        }
    }
}