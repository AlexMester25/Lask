package dev.alexmester.impl.domain.usecase

import dev.alexmester.impl.domain.repository.ProfileRepository
import dev.alexmester.models.news.NewsArticle
import kotlinx.coroutines.flow.Flow

class ObserveClappedArticlesUseCase(
    private val repository: ProfileRepository,
) {
    operator fun invoke(): Flow<List<NewsArticle>> =
        repository.observeClappedArticles()
}