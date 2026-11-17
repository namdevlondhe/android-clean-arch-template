package dev.namdev.template.core.database.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.namdev.template.core.database.PostDao
import dev.namdev.template.core.database.TemplateDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TemplateDatabase =
        Room.databaseBuilder(context, TemplateDatabase::class.java, "template.db").build()

    @Provides
    fun providePostDao(database: TemplateDatabase): PostDao = database.postDao()
}
