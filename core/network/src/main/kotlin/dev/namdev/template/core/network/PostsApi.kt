package dev.namdev.template.core.network

import kotlinx.serialization.Serializable
import retrofit2.http.GET

interface PostsApi {
    @GET("posts")
    suspend fun getPosts(): List<NetworkPost>
}

/** Wire format. Kept separate from the domain model so API changes stay in this module. */
@Serializable
data class NetworkPost(
    val id: Int,
    val userId: Int,
    val title: String,
    val body: String,
)
