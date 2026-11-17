package dev.namdev.template.core.database

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: Int,
    val userId: Int,
    val title: String,
    val body: String,
)

@Dao
interface PostDao {
    @Query("SELECT * FROM posts")
    fun observeAll(): Flow<List<PostEntity>>

    @Upsert
    suspend fun upsertAll(posts: List<PostEntity>)
}

@Database(entities = [PostEntity::class], version = 1, exportSchema = false)
abstract class TemplateDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
}
