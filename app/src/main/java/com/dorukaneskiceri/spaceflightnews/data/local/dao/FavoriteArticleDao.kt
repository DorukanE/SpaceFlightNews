package com.dorukaneskiceri.spaceflightnews.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dorukaneskiceri.spaceflightnews.data.local.db.entity.FavoriteArticleEntity

@Dao
interface FavoriteArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteArticle(favoriteArticle: FavoriteArticleEntity)

    @Query("DELETE FROM favorite_articles WHERE articleId = :articleId")
    suspend fun deleteFavoriteArticle(articleId: Int)

    @Query("SELECT * FROM favorite_articles WHERE articleId = :articleId")
    suspend fun getFavoriteArticle(articleId: Int): FavoriteArticleEntity?

    @Query("SELECT * FROM favorite_articles")
    suspend fun getFavoriteArticles(): List<FavoriteArticleEntity>
}