package com.dorukaneskiceri.spaceflightnews.domain.repository

import com.dorukaneskiceri.spaceflightnews.domain.model.Article
import com.dorukaneskiceri.spaceflightnews.domain.model.ArticleList
import com.dorukaneskiceri.spaceflightnews.util.BaseResult
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow

interface ArticleRepository {

    val gson: Gson
        get() = Gson()

    fun getArticles(search: String?, limit: Int?, offset: Int?): Flow<BaseResult<Article>>

    suspend fun saveArticleResponseToDatabase(articleResponse: Article)

    suspend fun getArticleResponseFromDatabase(): Flow<Article>

    suspend fun updateArticleFavoriteStatus(articleId: Int, isFavorite: Boolean)

    suspend fun getFavoriteArticles(): Flow<List<ArticleList>>
}