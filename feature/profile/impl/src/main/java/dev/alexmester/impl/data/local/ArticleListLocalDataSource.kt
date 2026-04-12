package dev.alexmester.impl.data.local

import dev.alexmester.database.dao.ArticleUserStateDao
import dev.alexmester.database.entity.ArticleEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class ArticleListLocalDataSource(
    private val userStateDao: ArticleUserStateDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    fun observeReadArticles(): Flow<List<ArticleEntity>> =
        userStateDao.observeReadArticles()

    fun observeClappedArticles(): Flow<List<ArticleEntity>> =
        userStateDao.observeClappedArticles()
}