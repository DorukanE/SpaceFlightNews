package com.dorukaneskiceri.spaceflightnews.domain.model

data class ArticleList(
    val id: Int,
    val title: String,
    val authors: List<Author>,
    val url: String,
    val imageUrl: String,
    val newsSite: String,
    val summary: String,
    val publishedAt: String,
    val updatedAt: String,
    val featured: Boolean,
    var isFavorite: Boolean,
)
