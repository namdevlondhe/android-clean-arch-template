package dev.namdev.template.core.domain

import dev.namdev.template.core.model.Post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObservePostsUseCase @Inject constructor(
    private val repository: PostRepository,
) {
    /** Posts sorted newest first (highest id first). */
    operator fun invoke(): Flow<List<Post>> =
        repository.observePosts().map { posts -> posts.sortedByDescending(Post::id) }
}

class RefreshPostsUseCase @Inject constructor(
    private val repository: PostRepository,
) {
    suspend operator fun invoke(): Result<Unit> = repository.refresh()
}
