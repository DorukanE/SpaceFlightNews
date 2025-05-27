package com.dorukaneskiceri.spaceflightnews.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dorukaneskiceri.spaceflightnews.data.local.dao.ArticleDao
import com.dorukaneskiceri.spaceflightnews.data.local.db.entity.ArticleEntity
import com.dorukaneskiceri.spaceflightnews.data.local.db.entity.ArticleResponseEntity
import com.dorukaneskiceri.spaceflightnews.data.local.db.entity.AuthorEntity
import com.dorukaneskiceri.spaceflightnews.data.local.db.entity.FavoriteArticleEntity

@Database(
    entities = [ArticleResponseEntity::class, ArticleEntity::class, AuthorEntity::class, FavoriteArticleEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SpaceFlightDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
}