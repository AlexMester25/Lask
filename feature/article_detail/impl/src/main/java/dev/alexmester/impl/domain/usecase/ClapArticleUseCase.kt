package dev.alexmester.impl.domain.usecase

import dev.alexmester.domain.repository.ArticleRepository
import dev.alexmester.domain.repository.UserPreferencesRepository
import dev.alexmester.utils.constants.LaskConstants.XP_PER_CLAP

class ClapArticleUseCase(
    private val articleRepository: ArticleRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    suspend operator fun invoke(articleId: Long) {
        articleRepository.addClap(articleId)
        userPreferencesRepository.addXp(XP_PER_CLAP)
    }
}