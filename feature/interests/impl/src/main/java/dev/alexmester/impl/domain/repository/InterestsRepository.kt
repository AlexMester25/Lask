package dev.alexmester.impl.domain.repository

interface InterestsRepository {
    suspend fun addInterest(keyWord: String)
    suspend fun removeInterest(keyWord: String)
}