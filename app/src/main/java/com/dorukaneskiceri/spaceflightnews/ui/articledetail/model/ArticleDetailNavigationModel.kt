package com.dorukaneskiceri.spaceflightnews.ui.articledetail.model

import kotlinx.serialization.Serializable

@Serializable
data class ArticleDetailNavigationModel(
    val itemId: Int,
    val title: String,
    val authors: List<String>,
    val imageUrl: String,
    val summary: String,
    val publishedAt: String,
    val updatedAt: String,
    val newsSiteUrl: String,
    val isFavorite: Boolean,
)
