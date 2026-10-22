package dev.alexmester.impl.domain.usecase

import dev.alexmester.domain.progress.XpRewards
import dev.alexmester.domain.repository.ArticleRepository
import dev.alexmester.domain.repository.UserPreferencesRepository

class MarkArticleAsReadUseCase(
    private val articleRepository: ArticleRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    suspend operator fun invoke(articleId: Long) {
        if (articleRepository.markAsRead(articleId)) {
            userPreferencesRepository.addXp(XpRewards.PER_READ)
        }
    }
}