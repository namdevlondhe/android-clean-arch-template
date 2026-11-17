package dev.namdev.template.core.data

import dev.namdev.template.core.data.di.IoDispatcher
import dev.namdev.template.core.database.PostDao
import dev.namdev.template.core.database.PostEntity
import dev.namdev.template.core.domain.PostRepository
import dev.namdev.template.core.model.Post
import dev.namdev.template.core.network.NetworkPost
import dev.namdev.template.core.network.PostsApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

/**
 * Offline-first: the UI always reads from Room; [refresh] syncs the network into Room.
 */
class OfflineFirstPostRepository @Inject constructor(
    private val api: PostsApi,
    private val dao: PostDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : PostRepository {

    override fun observePosts(): Flow<List<Post>> =
        dao.observeAll().map { entities -> entities.map(PostEntity::asModel) }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun refresh(): Result<Unit> = withContext(ioDispatcher) {
        try {
            val remote = api.getPosts()
            dao.upsertAll(remote.map(NetworkPost::asEntity))
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

internal fun NetworkPost.asEntity() = PostEntity(id = id, userId = userId, title = title, body = body)

internal fun PostEntity.asModel() = Post(id = id, userId = userId, title = title, body = body)
