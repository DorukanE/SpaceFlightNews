package com.dorukaneskiceri.spaceflightnews.data.model

import com.dorukaneskiceri.spaceflightnews.domain.model.Article
import com.dorukaneskiceri.spaceflightnews.domain.model.ArticleList
import com.dorukaneskiceri.spaceflightnews.domain.model.Author
import com.dorukaneskiceri.spaceflightnews.util.ext.BACKEND_DATE_TIME
import com.dorukaneskiceri.spaceflightnews.util.ext.DISPLAY_DATE_TIME
import com.dorukaneskiceri.spaceflightnews.util.ext.changeDateFormat
import com.dorukaneskiceri.spaceflightnews.util.ext.replaceHttpWithHttps
import com.google.gson.annotations.SerializedName

data class ArticleResponse(
    @SerializedName("count") val count: Int,
    @SerializedName("next") val next: String?,
    @SerializedName("previous") val previous: String?,
    @SerializedName("results") val results: List<ArticleListResponse>
)

data class ArticleListResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("authors") val authors: List<AuthorResponse>,
    @SerializedName("url") val url: String,
    @SerializedName("image_url") val imageUrl: String,
    @SerializedName("news_site") val newsSite: String,
    @SerializedName("summary") val summary: String,
    @SerializedName("published_at") val publishedAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("featured") val featured: Boolean,
    @Transient var isFavorite: Boolean
)

data class AuthorResponse(
    @SerializedName("name") val name: String,
)

fun ArticleResponse.toDomainModel() = Article(
    count = count,
    next = next,
    previous = previous,
    results = results.map { it.toDomainModel() }
)

fun ArticleListResponse.toDomainModel() = ArticleList(
    id = id,
    title = title,
    authors = authors.map { it.toDomainModel() },
    url = url,
    imageUrl = imageUrl.replaceHttpWithHttps(),
    newsSite = newsSite,
    summary = summary,
    publishedAt = changeDateFormat(publishedAt, BACKEND_DATE_TIME, DISPLAY_DATE_TIME),
    updatedAt = changeDateFormat(publishedAt, BACKEND_DATE_TIME, DISPLAY_DATE_TIME),
    featured = featured,
    isFavorite = isFavorite
)

fun AuthorResponse.toDomainModel() = Author(name = name)