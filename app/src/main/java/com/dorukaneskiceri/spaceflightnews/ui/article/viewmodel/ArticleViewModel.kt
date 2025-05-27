package com.dorukaneskiceri.spaceflightnews.ui.article.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dorukaneskiceri.spaceflightnews.domain.model.ArticleList
import com.dorukaneskiceri.spaceflightnews.domain.repository.ArticleRepository
import com.dorukaneskiceri.spaceflightnews.ui.article.model.ArticleUiState
import com.dorukaneskiceri.spaceflightnews.ui.article.model.ArticleUiState.Companion.initial
import com.dorukaneskiceri.spaceflightnews.util.NetworkException
import com.dorukaneskiceri.spaceflightnews.util.ext.onFailure
import com.dorukaneskiceri.spaceflightnews.util.ext.onSuccess
import com.dorukaneskiceri.spaceflightnews.util.ext.runWithIO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticleViewModel @Inject constructor(
    private val articleRepository: ArticleRepository,
) : ViewModel() {

    private var _state = MutableStateFlow(initial())
    val state = _state.onStart {
        callOnStartServices()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = initial()
    )

    private var _searchQuery = MutableStateFlow("")

    private val _offset = MutableStateFlow(0)

    companion object {
        private const val TAG = "ArticleViewModel"
        private const val DEFAULT_OFFSET = 10
        private const val THRESHOLD = 1000L
    }

    private suspend fun getArticles(
        search: String? = null,
        limit: Int? = null,
        offset: Int? = null,
        isRefreshed: Boolean = false,
        cameFromSearchPagination: Boolean = false,
    ) {
        if (!search.isNullOrBlank()) {
            delay(THRESHOLD)
        }
        articleRepository.getArticles(search, limit, offset)
            .onStart {
                _state.update {
                    it.copy(isLoading = true)
                }
            }.onCompletion {
                _state.update {
                    it.copy(isLoading = false)
                }
            }.onSuccess { response ->
                if (search.isNullOrBlank()) {
                    if (isRefreshed) {
                        _state.update {
                            it.copy(
                                articles = response.results,
                                isConnectionAvailable = true,
                                searchResults = emptyList()
                            )
                        }
                    } else {
                        _state.update {
                            val updatedArticleList = it.articles + response.results
                            articleRepository.saveArticleResponseToDatabase(
                                articleResponse = response.copy(
                                    results = updatedArticleList
                                )
                            )
                            articleRepository.getFavoriteArticles()
                                .collectLatest { favoriteArticles ->
                                    updatedArticleList.forEach {
                                        it.isFavorite =
                                            favoriteArticles.any { favoriteArticle ->
                                                favoriteArticle.id == it.id
                                            }
                                    }
                                }
                            it.copy(
                                articles = updatedArticleList,
                                isConnectionAvailable = true,
                                searchResults = emptyList()
                            )
                        }
                    }
                } else {
                    _state.update { currentState ->
                        currentState.copy(
                            articles = if (cameFromSearchPagination) currentState.searchResults + response.results else response.results,
                            searchResults = if (cameFromSearchPagination) currentState.searchResults + response.results else response.results,
                            isConnectionAvailable = true
                        )
                    }
                }
            }.onFailure { exception ->
                when (exception) {
                    is NetworkException.NetworkConnectionFailure -> {
                        _state.update {
                            it.copy(
                                errorMessage = exception.message,
                                isConnectionAvailable = false
                            )
                        }
                        articleRepository.getArticleResponseFromDatabase()
                            .collectLatest { cachedArticle ->
                                _state.update { it.copy(cachedArticles = cachedArticle.results.reversed()) }
                            }
                    }

                    else -> {
                        _state.update {
                            it.copy(errorMessage = exception.message)
                        }
                    }
                }
            }.collect()
    }

    private fun updateArticleList(articleId: Int, isFavorite: Boolean) {
        _state.update { currentState ->
            currentState.copy(
                articles = currentState.articles.map { article ->
                    if (article.id == articleId) {
                        article.copy(isFavorite = isFavorite)
                    } else {
                        article
                    }
                }
            )
        }
    }

    private fun searchInFavoriteArticles(
        query: String,
        currentState: ArticleUiState
    ): List<ArticleList> {
        val filteredArticles = currentState.favoriteArticles.filter {
            it.title.lowercase().contains(query, ignoreCase = true) || it.summary.lowercase()
                .contains(query, ignoreCase = true)
        }
        return filteredArticles
    }

    private suspend fun getFavoriteArticles() {
        articleRepository.getFavoriteArticles().collectLatest { favoriteArticles ->
            _state.update {
                it.copy(favoriteArticles = favoriteArticles)
            }
        }
    }

    fun refreshArticles() {
        viewModelScope.runWithIO {
            _offset.value = 0
            _searchQuery.value = ""
            _state.update {
                it.copy(searchQuery = _searchQuery.value)
            }
            getArticles(isRefreshed = true, offset = null, search = null)
        }
    }

    fun getMoreArticles(query: String? = null) {
        viewModelScope.runWithIO {
            _offset.value += DEFAULT_OFFSET
            getArticles(
                search = query,
                offset = _offset.value,
                cameFromSearchPagination = !query.isNullOrBlank()
            )
        }
    }

    fun searchArticles(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                refreshArticles()
            } else {
                _searchQuery.value = query
                _state.update {
                    it.copy(searchQuery = _searchQuery.value)
                }
                getArticles(search = _searchQuery.value, offset = _offset.value)
            }
        }
    }

    fun onUpdateFavoriteArticleFromNews(article: ArticleList, isFavorite: Boolean) {
        viewModelScope.runWithIO {
            articleRepository.updateArticleFavoriteStatus(article.id, isFavorite)
                .also {
                    getFavoriteArticles()
                    updateArticleList(article.id, isFavorite)
                }
        }
    }

    fun onUpdateFavoriteArticleFromFavorites(article: ArticleList?, isFavorite: Boolean) {
        if (article == null) return
        viewModelScope.runWithIO {
            articleRepository.updateArticleFavoriteStatus(article.id, isFavorite)
                .also {
                    getFavoriteArticles()
                    updateArticleList(article.id, isFavorite)
                }
        }
    }

    fun clearErrorState() {
        _state.update {
            it.copy(errorMessage = null)
        }
    }

    fun searchInFavorites(query: String) {
        _state.update { currentState ->
            currentState.copy(
                searchResultsFavorites = searchInFavoriteArticles(query, currentState)
            )
        }
    }

    private fun callOnStartServices() {
        viewModelScope.runWithIO {
            listOf(
                async { getArticles() },
                async { getFavoriteArticles() }
            ).awaitAll()
        }
    }
}