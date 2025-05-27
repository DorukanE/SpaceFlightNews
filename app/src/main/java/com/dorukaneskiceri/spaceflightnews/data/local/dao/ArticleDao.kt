package com.dorukaneskiceri.spaceflightnews.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dorukaneskiceri.spaceflightnews.data.local.db.entity.ArticleEntity
import com.dorukaneskiceri.spaceflightnews.data.local.db.entity.ArticleResponseEntity
import com.dorukaneskiceri.spaceflightnews.data.local.db.entity.AuthorEntity

@Dao
interface ArticleDao : FavoriteArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticleResponse(article: ArticleResponseEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: ArticleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuthor(author: AuthorEntity)

    @Query("SELECT * FROM article_response")
    suspend fun getArticleResponse(): ArticleResponseEntity

    @Query("SELECT * FROM articles")
    suspend fun getArticles(): List<ArticleEntity>

    @Query("SELECT * FROM authors")
    suspend fun getAuthors(): List<AuthorEntity>

    @Query("UPDATE articles SET isFavorite = :isFavorite WHERE id = :articleId")
    suspend fun updateFavoriteStatus(articleId: Int, isFavorite: Boolean)
}