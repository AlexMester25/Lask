package dev.alexmester.impl.domain.usecase

import dev.alexmester.datastore.UserPreferencesDataSource
import dev.alexmester.utils.constants.LaskConstants
import kotlinx.coroutines.flow.first

class GetInterestsExploreUseCase(
    private val preferencesDataSource: UserPreferencesDataSource,
) {
    suspend operator fun invoke(): Pair<String, String> {
        val prefs = preferencesDataSource.userPreferences.first()

        val query = prefs.interests.joinToString(separator = LaskConstants.SEPARATOR_OR)

        return query to prefs.defaultLanguage
    }
}