package dev.alexmester.impl.domain.usecase

import dev.alexmester.domain.progress.XpRewards
import dev.alexmester.domain.repository.ArticleRepository
import dev.alexmester.domain.repository.UserPreferencesRepository

class ClapArticleUseCase(
    private val articleRepository: ArticleRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    suspend operator fun invoke(articleId: Long) {
        articleRepository.addClap(articleId)
        userPreferencesRepository.addXp(XpRewards.PER_CLAP)
    }
}