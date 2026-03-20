package dev.alexmester.posts.domain.repository

import dev.alexmester.posts.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    fun getPosts(): Flow<List<Post>>
    suspend fun refreshPosts(): Result<Unit>
    suspend fun getPostById(postId: Int): Post?
}