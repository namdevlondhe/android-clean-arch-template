package dev.namdev.template.core.domain

import dev.namdev.template.core.model.Post
import kotlinx.coroutines.flow.Flow

/**
 * Contract owned by the domain layer; implemented in core:data.
 * The local database is the single source of truth.
 */
interface PostRepository {
    fun observePosts(): Flow<List<Post>>

    /** Fetches fresh data from the network and stores it locally. */
    suspend fun refresh(): Result<Unit>
}
