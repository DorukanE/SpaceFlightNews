package com.dorukaneskiceri.spaceflightnews.ui.article.screen

import CustomSearchBar
import ErrorBottomSheet
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.dorukaneskiceri.spaceflightnews.R
import com.dorukaneskiceri.spaceflightnews.domain.model.ArticleList
import com.dorukaneskiceri.spaceflightnews.domain.model.Author
import com.dorukaneskiceri.spaceflightnews.ui.article.component.CustomProgressIndicator
import com.dorukaneskiceri.spaceflightnews.ui.article.component.CustomTopAppBar
import com.dorukaneskiceri.spaceflightnews.ui.article.model.NewsItemUiModel
import com.dorukaneskiceri.spaceflightnews.ui.article.viewmodel.ArticleViewModel
import com.dorukaneskiceri.spaceflightnews.ui.articledetail.model.ArticleDetailNavigationModel
import com.dorukaneskiceri.spaceflightnews.ui.theme.SpaceFlightNewsTheme
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val VISIBLE_ITEM_THRESHOLD = 10
private const val FIRST_ITEM_INDEX = 0
private const val ROUNDED_PERCENTAGE = 100

@Composable
fun ArticleScreen(
    modifier: Modifier = Modifier,
    onGoToDetailScreen: (ArticleDetailNavigationModel) -> Unit = {},
    viewModel: ArticleViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val articleList: List<ArticleList> = state.articles.ifEmpty {
        state.cachedArticles
    }

    ArticleScreenImpl(
        modifier = modifier.fillMaxSize(),
        articles = articleList,
        favoriteArticles = state.favoriteArticles,
        loadingState = state.isLoading,
        errorMessage = state.errorMessage,
        isConnectionAvailable = state.isConnectionAvailable,
        onGoToDetailScreen = onGoToDetailScreen,
        onGetArticles = viewModel::getMoreArticles,
        onRefreshArticles = viewModel::refreshArticles,
        onClearErrorState = viewModel::clearErrorState,
        searchResults = state.searchResults,
        onSearchArticles = viewModel::searchArticles,
        searchQuery = state.searchQuery,
        onFavoriteClickForNews = viewModel::onUpdateFavoriteArticleFromNews,
        onFavoriteItemClickForFavorites = viewModel::onUpdateFavoriteArticleFromFavorites,
        onSearchInFavorites = viewModel::searchInFavorites,
        searchResultsFavorites = state.searchResultsFavorites
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleScreenImpl(
    modifier: Modifier = Modifier,
    articles: List<ArticleList>,
    favoriteArticles: List<ArticleList>,
    loadingState: Boolean,
    isConnectionAvailable: Boolean,
    searchQuery: String,
    errorMessage: String? = null,
    searchResults: List<ArticleList>,
    searchResultsFavorites: List<ArticleList>,
    onGoToDetailScreen: (ArticleDetailNavigationModel) -> Unit,
    onGetArticles: (String) -> Unit,
    onRefreshArticles: () -> Unit,
    onClearErrorState: () -> Unit,
    onSearchArticles: (String) -> Unit,
    onFavoriteClickForNews: (ArticleList, Boolean) -> Unit,
    onFavoriteItemClickForFavorites: (ArticleList?, Boolean) -> Unit,
    onSearchInFavorites: (String) -> Unit,
) {
    val isRefreshing by rememberSaveable { mutableStateOf(false) }
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)
    var searchInputNews by rememberSaveable { mutableStateOf("") }
    var searchInputFavorites by rememberSaveable { mutableStateOf("") }
    var currentPage by rememberSaveable { mutableIntStateOf(FIRST_ITEM_INDEX) }
    var previousPage by rememberSaveable { mutableIntStateOf(FIRST_ITEM_INDEX) }

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = {
            onRefreshArticles()
        }
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
        ) {
            CustomTopAppBar(title = stringResource(R.string.article_screen_title))
            Spacer(Modifier.height(SpaceFlightNewsTheme.spacing.small))

            when (currentPage) {
                FIRST_ITEM_INDEX -> CustomSearchBar(
                    query = searchInputNews,
                    placeholder = stringResource(R.string.search_news_placeholder),
                    onQueryChange = {
                        searchInputNews = it
                        onSearchArticles(searchInputNews)
                    }
                )

                else -> CustomSearchBar(
                    query = searchInputFavorites,
                    placeholder = stringResource(R.string.search_favorites_placeholder),
                    onQueryChange = {
                        searchInputFavorites = it
                        onSearchInFavorites(searchInputFavorites)
                    }
                )
            }

            Spacer(Modifier.height(SpaceFlightNewsTheme.spacing.small))

            NewsPager(
                articles = searchResults.ifEmpty { articles },
                favoriteArticles = favoriteArticles,
                loadingState = loadingState,
                isConnectionAvailable = isConnectionAvailable,
                onGetArticles = onGetArticles,
                searchQuery = searchQuery,
                searchResultsFavorites = searchResultsFavorites,
                onFavoriteClickForNews = { favoriteArticle, isFavorite ->
                    onFavoriteClickForNews(favoriteArticle, isFavorite)
                },
                onFavoriteItemClickForFavorites = { favoriteArticle, isFavorite ->
                    onFavoriteItemClickForFavorites(favoriteArticle, isFavorite)
                },
                onGoToDetailScreen = onGoToDetailScreen,
                currentPage = { page ->
                    if (page != previousPage) {
                        searchInputNews = ""
                        searchInputFavorites = ""
                    }
                    currentPage = page
                    previousPage = page
                }
            )
        }
    }

    ErrorBottomSheet(
        isShowBottomSheet = errorMessage != null,
        title = errorMessage.orEmpty(),
        onButtonClick = {
            onClearErrorState()
        },
        onDismissRequest = {
            onClearErrorState()
        }
    )

    CustomProgressIndicator(
        isVisible = loadingState,
    )
}

@Composable
fun NewsPager(
    articles: List<ArticleList>,
    favoriteArticles: List<ArticleList>,
    searchQuery: String,
    loadingState: Boolean,
    isConnectionAvailable: Boolean,
    searchResultsFavorites: List<ArticleList>,
    onGetArticles: (String) -> Unit,
    onFavoriteClickForNews: (ArticleList, Boolean) -> Unit,
    onFavoriteItemClickForFavorites: (ArticleList?, Boolean) -> Unit,
    onGoToDetailScreen: (ArticleDetailNavigationModel) -> Unit,
    currentPage: (Int) -> Unit
) {
    val articleListState = rememberLazyListState()
    val favoriteArticleListState = rememberLazyListState()
    val pages = listOf("News", "Favorites")
    val pagerState = rememberPagerState { pages.size }
    val coroutineScope = rememberCoroutineScope()
    var showScrollToTopButton by rememberSaveable { mutableStateOf(false) }
    var resetVisibleItemCounter by rememberSaveable { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            indicator = { tabPositions ->
                if (tabPositions.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[pagerState.currentPage])
                            .padding(horizontal = SpaceFlightNewsTheme.spacing.large)
                    ) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.primary,
                            thickness = 2.dp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            divider = {
                HorizontalDivider(thickness = SpaceFlightNewsTheme.spacing.minDp)
            }
        ) {
            pages.forEachIndexed { index, title ->
                Box(modifier = Modifier.padding(horizontal = SpaceFlightNewsTheme.spacing.small)) {
                    Tab(
                        text = { Text(title) },
                        selected = pagerState.currentPage == index,
                        onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                        unselectedContentColor = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
        ) { page ->
            when (page) {
                FIRST_ITEM_INDEX -> {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        NewsListScreen(
                            articles = articles.toUiModel(),
                            searchQuery = searchQuery,
                            onNewsClick = {
                                onGoToDetailScreen(it.toArticleDetailNavigationModel())
                            },
                            onFavoriteClick = { itemId, isFavorite ->
                                val favoriteArticle = articles.first { it.id == itemId }
                                onFavoriteClickForNews(favoriteArticle, isFavorite)
                            },
                            showScrollToTopButton = {
                                showScrollToTopButton = it
                            },
                            listState = articleListState,
                            loadingState = loadingState,
                            resetVisibleItemCounter = resetVisibleItemCounter,
                            isConnectionAvailable = isConnectionAvailable,
                            onGetArticles = onGetArticles,
                        )

                        if (showScrollToTopButton) {
                            FloatingActionButton(
                                onClick = {
                                    coroutineScope.launch {
                                        articleListState.animateScrollToItem(FIRST_ITEM_INDEX)
                                        showScrollToTopButton = false
                                        resetVisibleItemCounter = true
                                    }
                                },
                                containerColor = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(ROUNDED_PERCENTAGE),
                                modifier = Modifier.padding(
                                    start = SpaceFlightNewsTheme.spacing.extraLarge,
                                    end = SpaceFlightNewsTheme.spacing.extraLarge,
                                    bottom = SpaceFlightNewsTheme.spacing.huge
                                )
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.ic_rocket),
                                    contentDescription = "Scroll to top button"
                                )
                            }
                        }
                    }
                }

                else -> {
                    if (favoriteArticles.isNotEmpty()) {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            FavoriteNewsListScreen(
                                favoriteArticles = favoriteArticles.toUiModel(),
                                searchResultsFavorites = searchResultsFavorites.toUiModel(),
                                onNewsClick = {
                                    onGoToDetailScreen(it.toArticleDetailNavigationModel())
                                },
                                onFavoriteClick = { itemId, isFavorite ->
                                    val favoriteArticle = articles.firstOrNull { it.id == itemId }
                                    onFavoriteItemClickForFavorites(favoriteArticle, isFavorite)
                                },
                                showScrollToTopButton = {
                                    showScrollToTopButton = it
                                },
                                listState = favoriteArticleListState,
                            )

                            if (showScrollToTopButton) {
                                FloatingActionButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            favoriteArticleListState.animateScrollToItem(
                                                FIRST_ITEM_INDEX
                                            )
                                            showScrollToTopButton = false
                                            resetVisibleItemCounter = true
                                        }
                                    },
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(ROUNDED_PERCENTAGE),
                                    modifier = Modifier.padding(
                                        start = SpaceFlightNewsTheme.spacing.extraLarge,
                                        end = SpaceFlightNewsTheme.spacing.extraLarge,
                                        bottom = SpaceFlightNewsTheme.spacing.huge
                                    )
                                ) {
                                    Image(
                                        painter = painterResource(R.drawable.ic_rocket),
                                        contentDescription = "Scroll to top button"
                                    )
                                }
                            }
                        }
                    } else {
                        FavoritesEmptyView()
                    }
                }
            }
            currentPage(pagerState.settledPage)
        }
    }
}


@Composable
fun FavoriteNewsListScreen(
    favoriteArticles: List<NewsItemUiModel>,
    searchResultsFavorites: List<NewsItemUiModel>,
    listState: LazyListState,
    onNewsClick: (NewsItemUiModel) -> Unit,
    onFavoriteClick: (Int, Boolean) -> Unit,
    showScrollToTopButton: (Boolean) -> Unit,
) {
    var visibleItemCount by remember { mutableIntStateOf(0) }
    var items by remember { mutableStateOf(searchResultsFavorites.ifEmpty { favoriteArticles }) }

    LaunchedEffect(favoriteArticles, searchResultsFavorites) {
        items = searchResultsFavorites.ifEmpty { favoriteArticles }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(SpaceFlightNewsTheme.spacing.small),
        contentPadding = PaddingValues(
            vertical = SpaceFlightNewsTheme.spacing.small,
        ),
        state = listState
    ) {
        items(count = items.size, key = { index -> items[index].modelId }) { index ->
            val newsItem = items[index]
            NewsCard(
                newsItemUiModel = newsItem,
                onClick = { onNewsClick(newsItem) },
                onFavoriteClick = { itemId, isFavorite ->
                    onFavoriteClick(itemId, isFavorite)
                }
            )
            LaunchedEffect(Unit) {
                snapshotFlow {
                    listState.layoutInfo.visibleItemsInfo.any { it.index == index }
                }.collectLatest { isVisible ->
                    if (isVisible) {
                        visibleItemCount = maxOf(visibleItemCount, index + 1)
                    }
                }
            }
        }
    }

    LaunchedEffect(visibleItemCount) {
        if (visibleItemCount >= VISIBLE_ITEM_THRESHOLD) {
            showScrollToTopButton(true)
        }
    }
}

@Composable
fun NewsListScreen(
    articles: List<NewsItemUiModel>,
    resetVisibleItemCounter: Boolean,
    loadingState: Boolean,
    isConnectionAvailable: Boolean,
    searchQuery: String,
    listState: LazyListState,
    onNewsClick: (NewsItemUiModel) -> Unit,
    onFavoriteClick: (Int, Boolean) -> Unit,
    showScrollToTopButton: (Boolean) -> Unit,
    onGetArticles: (String) -> Unit,
) {
    var visibleItemCount by remember { mutableIntStateOf(0) }
    var items by remember { mutableStateOf(articles) }
    var lastItemReached by remember { mutableStateOf(false) }

    LaunchedEffect(articles) {
        items = articles
    }

    LaunchedEffect(resetVisibleItemCounter) {
        if (resetVisibleItemCounter) {
            visibleItemCount = FIRST_ITEM_INDEX
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(SpaceFlightNewsTheme.spacing.small),
        contentPadding = PaddingValues(
            vertical = SpaceFlightNewsTheme.spacing.small,
        ),
        state = listState
    ) {
        items(count = items.size, key = { index -> items[index].modelId }) { index ->
            val newsItem = items[index]
            NewsCard(
                newsItemUiModel = newsItem,
                onClick = { onNewsClick(newsItem) },
                onFavoriteClick = { itemId, isFavorite ->
                    onFavoriteClick(itemId, isFavorite)
                }
            )

            LaunchedEffect(Unit) {
                snapshotFlow {
                    listState.layoutInfo.visibleItemsInfo.any { it.index == index }
                }.collectLatest { isVisible ->
                    if (isVisible) {
                        visibleItemCount = maxOf(visibleItemCount, index + 1)
                    }
                    if (isConnectionAvailable && isVisible && index == items.lastIndex && !loadingState) {
                        lastItemReached = true
                    }
                    if (isConnectionAvailable && isVisible && index == items.lastIndex - 2 && !loadingState && !lastItemReached) {
                        onGetArticles(searchQuery)
                        lastItemReached = false
                    }
                }
            }
        }
    }

    LaunchedEffect(visibleItemCount) {
        if (visibleItemCount >= VISIBLE_ITEM_THRESHOLD) {
            showScrollToTopButton(true)
        }
    }
}

@Composable
fun NewsCard(
    newsItemUiModel: NewsItemUiModel,
    onClick: () -> Unit,
    onFavoriteClick: (Int, Boolean) -> Unit
) {
    var isFavorite by rememberSaveable { mutableStateOf(newsItemUiModel.isFavorite) }
    val transition = updateTransition(targetState = isFavorite, label = "favoriteTransition")
    val animatedScale by transition.animateFloat(
        transitionSpec = {
            tween(durationMillis = 300, easing = EaseInOut)
        },
        label = "scaleAnimation"
    ) { state ->
        if (state) 1.2f else 1f
    }

    LaunchedEffect(newsItemUiModel.isFavorite) {
        isFavorite = newsItemUiModel.isFavorite
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(SpaceFlightNewsTheme.spacing.small)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = SpaceFlightNewsTheme.spacing.extraSmall)
    ) {
        Column(modifier = Modifier.padding(SpaceFlightNewsTheme.spacing.medium)) {
            AsyncImage(
                model = newsItemUiModel.imageUrl,
                error = painterResource(R.drawable.ic_image_placeholder),
                placeholder = painterResource(R.drawable.ic_image_placeholder),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(SpaceFlightNewsTheme.spacing.small))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = newsItemUiModel.title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = {
                    isFavorite = !isFavorite
                    onFavoriteClick(newsItemUiModel.itemId, isFavorite)
                }) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color.Red else Color.Gray,
                        modifier = Modifier.scale(animatedScale)
                    )
                }
            }

            Spacer(modifier = Modifier.height(SpaceFlightNewsTheme.spacing.small))
            Text(
                text = newsItemUiModel.summary,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(SpaceFlightNewsTheme.spacing.small))
            Text(
                text = "Publish Date: ${newsItemUiModel.publishedAt}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
fun FavoritesEmptyView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(R.drawable.ic_add_favorite),
                contentDescription = "Add some favorites"
            )
            Spacer(Modifier.height(SpaceFlightNewsTheme.spacing.medium))
            Text(text = "You haven't added any favorites yet.")
        }
    }
}

private fun List<ArticleList>.toUiModel(): List<NewsItemUiModel> {
    return this.map {
        NewsItemUiModel(
            itemId = it.id,
            title = it.title,
            authors = it.authors.map { it.name },
            imageUrl = it.imageUrl,
            summary = it.summary,
            publishedAt = it.publishedAt,
            updatedAt = it.updatedAt,
            newsSiteUrl = it.url,
            isFavorite = it.isFavorite
        )
    }
}

private fun NewsItemUiModel.toArticleDetailNavigationModel(): ArticleDetailNavigationModel {
    return ArticleDetailNavigationModel(
        itemId = this.itemId,
        title = this.title,
        authors = this.authors,
        imageUrl = this.imageUrl,
        summary = this.summary,
        publishedAt = this.publishedAt,
        updatedAt = this.updatedAt,
        newsSiteUrl = this.newsSiteUrl,
        isFavorite = this.isFavorite
    )
}

@Preview(showBackground = true)
@Composable
fun ArticleScreenPreviewWithData() {
    val sampleArticles = listOf(
        ArticleList(
            id = 1,
            title = "Sample Article 1",
            authors = listOf(Author("Author 1")),
            url = "https://example.com/article1",
            imageUrl = "https://example.com/image1.jpg",
            newsSite = "Sample Site 1",
            summary = "This is a sample article summary 1.",
            publishedAt = "2024-01-01T12:00:00Z",
            updatedAt = "2024-01-01T13:00:00Z",
            featured = true,
            isFavorite = false
        ),
        ArticleList(
            id = 2,
            title = "Sample Article 2",
            authors = listOf(Author("Author 2")),
            url = "https://example.com/article2",
            imageUrl = "https://example.com/image2.jpg",
            newsSite = "Sample Site 2",
            summary = "This is a sample article summary 2.",
            publishedAt = "2024-01-02T12:00:00Z",
            updatedAt = "2024-01-02T13:00:00Z",
            featured = false,
            isFavorite = true
        )
    )

    ArticleScreenImpl(
        modifier = Modifier.fillMaxSize(),
        articles = sampleArticles,
        favoriteArticles = emptyList(),
        loadingState = false,
        isConnectionAvailable = true,
        onGoToDetailScreen = {},
        onGetArticles = {},
        onClearErrorState = {},
        onRefreshArticles = {},
        onSearchArticles = {},
        searchResults = emptyList(),
        searchQuery = "",
        onFavoriteClickForNews = { _, _ -> },
        onFavoriteItemClickForFavorites = { _, _ -> },
        onSearchInFavorites = {},
        searchResultsFavorites = emptyList()
    )
}
