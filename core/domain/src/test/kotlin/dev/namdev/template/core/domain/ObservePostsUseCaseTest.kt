package dev.namdev.template.core.domain

import dev.namdev.template.core.model.Post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ObservePostsUseCaseTest {

    @Test
    fun `posts are sorted newest first`() = runTest {
        val repository = object : PostRepository {
            override fun observePosts(): Flow<List<Post>> =
                flowOf(listOf(post(1), post(3), post(2)))

            override suspend fun refresh(): Result<Unit> = Result.success(Unit)
        }

        val result = ObservePostsUseCase(repository)().first()

        assertEquals(listOf(3, 2, 1), result.map(Post::id))
    }

    private fun post(id: Int) = Post(id = id, userId = 1, title = "t$id", body = "b$id")
}
