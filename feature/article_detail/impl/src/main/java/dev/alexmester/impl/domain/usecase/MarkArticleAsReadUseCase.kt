package dev.alexmester.impl.domain.usecase

import dev.alexmester.domain.repository.ArticleRepository
import dev.alexmester.domain.repository.UserPreferencesRepository
import dev.alexmester.utils.constants.LaskConstants.XP_PER_READ

class MarkArticleAsReadUseCase(
    private val articleRepository: ArticleRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    suspend operator fun invoke(articleId: Long) {
        if (articleRepository.markAsRead(articleId)) {
            userPreferencesRepository.addXp(XP_PER_READ)
        }
    }
}