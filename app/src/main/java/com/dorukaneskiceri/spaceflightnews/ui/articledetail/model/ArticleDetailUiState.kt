package com.dorukaneskiceri.spaceflightnews.ui.articledetail.model

import androidx.compose.runtime.Immutable

@Immutable
data class ArticleDetailUiState(
    val articleDetail: ArticleDetailNavigationModel? = null
) {
    companion object {
        fun initial() = ArticleDetailUiState(
            articleDetail = ArticleDetailNavigationModel(
                itemId = 0,
                title = "",
                imageUrl = "",
                newsSiteUrl = "",
                summary = "",
                publishedAt = "",
                updatedAt = "",
                authors = emptyList(),
                isFavorite = false
            )
        )
    }
}
