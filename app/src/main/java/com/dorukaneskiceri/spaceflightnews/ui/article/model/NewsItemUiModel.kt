package com.dorukaneskiceri.spaceflightnews.ui.article.model

import java.util.UUID

data class NewsItemUiModel(
    val modelId: String = UUID.randomUUID().toString(),
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