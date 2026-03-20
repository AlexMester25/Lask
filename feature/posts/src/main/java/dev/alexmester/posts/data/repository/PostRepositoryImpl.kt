package dev.alexmester.posts.data.repository


import dev.alexmester.database.dao.PostDao
import dev.alexmester.database.entity.PostEntity
import dev.alexmester.posts.data.remote.PostApiService
import dev.alexmester.posts.domain.model.Post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PostRepositoryImpl(
    private val postApiService: PostApiService,
    private val postDao: PostDao
) : dev.alexmester.posts.domain.repository.PostRepository {

    override fun getPosts(): Flow<List<Post>> {
        return postDao.getAllPosts().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun refreshPosts(): Result<Unit> {
        return try {
            val posts = postApiService.getPosts()
            val entities = posts.map { dto ->
                PostEntity(
                    id = dto.id,
                    userId = dto.userId,
                    title = dto.title,
                    body = dto.body
                )
            }
            postDao.insertPosts(entities)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPostById(postId: Int): Post? {
        return postDao.getPostById(postId)?.toDomain()
    }

    private fun PostEntity.toDomain(): Post {
        return Post(
            id = id,
            userId = userId,
            title = title,
            body = body
        )
    }
}