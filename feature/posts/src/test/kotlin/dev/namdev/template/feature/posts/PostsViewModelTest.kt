package dev.namdev.template.feature.posts

import dev.namdev.template.core.domain.ObservePostsUseCase
import dev.namdev.template.core.domain.PostRepository
import dev.namdev.template.core.domain.RefreshPostsUseCase
import dev.namdev.template.core.model.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PostsViewModelTest {

    private val mainDispatcher: TestDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() = Dispatchers.setMain(mainDispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `shows posts after a successful refresh`() = runTest {
        val repository = FakePostRepository(remote = { listOf(post(1)) })
        val viewModel = PostsViewModel(ObservePostsUseCase(repository), RefreshPostsUseCase(repository))
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        assertEquals(PostsUiState.Success(listOf(post(1)), isRefreshing = false), viewModel.uiState.value)
    }

    @Test
    fun `shows error when refresh fails and nothing is cached`() = runTest {
        val repository = FakePostRepository(remote = { error("No internet") })
        val viewModel = PostsViewModel(ObservePostsUseCase(repository), RefreshPostsUseCase(repository))
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        assertEquals(PostsUiState.Error("No internet"), viewModel.uiState.value)
    }

    private fun post(id: Int) = Post(id = id, userId = 1, title = "t$id", body = "b$id")
}

private class FakePostRepository(private val remote: () -> List<Post>) : PostRepository {
    private val cache = MutableStateFlow<List<Post>>(emptyList())

    override fun observePosts(): Flow<List<Post>> = cache

    override suspend fun refresh(): Result<Unit> = runCatching { cache.value = remote() }
}
