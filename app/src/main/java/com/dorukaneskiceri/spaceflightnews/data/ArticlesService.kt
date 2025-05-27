package com.dorukaneskiceri.spaceflightnews.data

import com.dorukaneskiceri.spaceflightnews.data.model.ArticleResponse
import com.dorukaneskiceri.spaceflightnews.util.BaseResult
import retrofit2.http.GET
import retrofit2.http.Query

interface ArticlesService {
    @GET("articles")
    suspend fun getArticles(
        @Query("search") search: String? = null,
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null,
    ): BaseResult<ArticleResponse>
}
