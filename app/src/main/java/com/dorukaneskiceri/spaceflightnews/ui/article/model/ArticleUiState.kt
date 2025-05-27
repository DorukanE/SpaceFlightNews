package com.dorukaneskiceri.spaceflightnews.ui.article.model

import androidx.compose.runtime.Immutable
import com.dorukaneskiceri.spaceflightnews.domain.model.ArticleList

@Immutable
data class ArticleUiState(
    val isLoading: Boolean,
    val articles: List<ArticleList>,
    val favoriteArticles: List<ArticleList>,
    val cachedArticles: List<ArticleList>,
    val errorMessage: String?,
    val isConnectionAvailable: Boolean,
    val searchResults: List<ArticleList>,
    val searchQuery: String,
    val searchResultsFavorites: List<ArticleList>
) {
    companion object {
        fun initial() = ArticleUiState(
            isLoading = false,
            articles = emptyList(),
            favoriteArticles = emptyList(),
            cachedArticles = emptyList(),
            errorMessage = null,
            isConnectionAvailable = true,
            searchResults = emptyList(),
            searchQuery = "",
            searchResultsFavorites = emptyList()
        )
    }
}
