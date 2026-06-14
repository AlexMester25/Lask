package dev.alexmester.api.navigation

import dev.alexmester.navigation.FeatureApi

interface TypedArticleListApi: FeatureApi {
    fun typedArticleListRoute(type: ArticleListType): TypedArticleListRoute
}