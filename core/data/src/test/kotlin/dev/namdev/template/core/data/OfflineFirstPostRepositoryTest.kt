package dev.namdev.template.core.data

import dev.namdev.template.core.database.PostDao
import dev.namdev.template.core.database.PostEntity
import dev.namdev.template.core.model.Post
import dev.namdev.template.core.network.NetworkPost
import dev.namdev.template.core.network.PostsApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class OfflineFirstPostRepositoryTest {

    private val dao = FakePostDao()

    @Test
    fun `refresh stores network posts in the database`() = runTest {
        val api = FakePostsApi { listOf(NetworkPost(id = 1, userId = 7, title = "Hello", body = "World")) }
        val repository = OfflineFirstPostRepository(api, dao, UnconfinedTestDispatcher(testScheduler))

        val result = repository.refresh()

        assertTrue(result.isSuccess)
        assertEquals(
            listOf(Post(id = 1, userId = 7, title = "Hello", body = "World")),
            repository.observePosts().first(),
        )
    }

    @Test
    fun `network failure returns failure and keeps cached data`() = runTest {
        dao.upsertAll(listOf(PostEntity(id = 5, userId = 1, title = "cached", body = "")))
        val api = FakePostsApi { throw IOException("offline") }
        val repository = OfflineFirstPostRepository(api, dao, UnconfinedTestDispatcher(testScheduler))

        val result = repository.refresh()

        assertTrue(result.isFailure)
        assertEquals(listOf(5), repository.observePosts().first().map(Post::id))
    }
}

private class FakePostsApi(private val response: () -> List<NetworkPost>) : PostsApi {
    override suspend fun getPosts(): List<NetworkPost> = response()
}

private class FakePostDao : PostDao {
    private val rows = MutableStateFlow<Map<Int, PostEntity>>(emptyMap())

    override fun observeAll(): Flow<List<PostEntity>> = rows.map { it.values.toList() }

    override suspend fun upsertAll(posts: List<PostEntity>) {
        rows.value = rows.value + posts.associateBy(PostEntity::id)
    }
}
