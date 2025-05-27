package com.dorukaneskiceri.spaceflightnews.data.local.di

import android.content.Context
import androidx.room.Room
import com.dorukaneskiceri.spaceflightnews.data.local.dao.ArticleDao
import com.dorukaneskiceri.spaceflightnews.data.local.db.SpaceFlightDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): SpaceFlightDatabase {
        return Room.databaseBuilder(
            context,
            SpaceFlightDatabase::class.java,
            "space_flight_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideArticleDao(database: SpaceFlightDatabase): ArticleDao {
        return database.articleDao()
    }
}