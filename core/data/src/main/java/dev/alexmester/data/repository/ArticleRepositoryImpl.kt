package dev.alexmester.data.repository

import dev.alexmester.database.dao.ArticleUserStateDao
import dev.alexmester.domain.repository.ArticleRepository
import kotlinx.coroutines.flow.Flow

class ArticleRepositoryImpl (
    private val userStateDao: ArticleUserStateDao,
): ArticleRepository {

    override fun observeReadArticleIds(): Flow<List<Long>> =
        userStateDao.observeReadArticleIds()
}