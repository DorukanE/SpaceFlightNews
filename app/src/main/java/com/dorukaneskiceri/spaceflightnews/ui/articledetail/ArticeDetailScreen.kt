package com.dorukaneskiceri.spaceflightnews.ui.articledetail

import ErrorBottomSheet
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.dorukaneskiceri.spaceflightnews.R
import com.dorukaneskiceri.spaceflightnews.ui.article.component.CustomTopAppBar
import com.dorukaneskiceri.spaceflightnews.ui.articledetail.model.ArticleDetailUiState
import com.dorukaneskiceri.spaceflightnews.ui.articledetail.viewmodel.ArticleDetailViewModel
import com.dorukaneskiceri.spaceflightnews.ui.theme.SpaceFlightNewsTheme

@Composable
fun ArticleDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: ArticleDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ArticleDetailScreenImpl(
        onNavigateBack = onNavigateBack,
        state = state,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailScreenImpl(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    state: ArticleDetailUiState
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var showErrorBottomSheet by rememberSaveable { mutableStateOf(state.articleDetail == null) }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = state.articleDetail?.title ?: "Article Detail",
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    if (state.articleDetail != null) {
                        IconButton(onClick = {
                            shareArticle(
                                context,
                                state.articleDetail.newsSiteUrl
                            )
                        }) {
                            Icon(
                                Icons.Filled.Share,
                                contentDescription = "Share",
                                tint = Color.White
                            )
                        }
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.surface,
    ) { paddingValues ->
        if (state.articleDetail != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = SpaceFlightNewsTheme.spacing.medium)
                    .verticalScroll(scrollState)
            ) {
                Spacer(modifier = Modifier.height(SpaceFlightNewsTheme.spacing.medium))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = SpaceFlightNewsTheme.spacing.medium)
                        .shadow(
                            elevation = 3.dp,
                            shape = MaterialTheme.shapes.extraLarge
                        ),
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(SpaceFlightNewsTheme.spacing.medium)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = state.articleDetail.title,
                                fontWeight = FontWeight.ExtraBold,
                                style = MaterialTheme.typography.headlineSmall.copy(fontSize = 26.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1f)
                            )
                            if (state.articleDetail.isFavorite) {
                                FavoriteIcon()
                            }
                        }
                        Spacer(modifier = Modifier.height(SpaceFlightNewsTheme.spacing.small))
                        Text(
                            text = "Published by ${state.articleDetail.authors.joinToString(", ")}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontStyle = FontStyle.Italic,
                                fontSize = 14.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(SpaceFlightNewsTheme.spacing.extraSmall))
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(bottom = SpaceFlightNewsTheme.spacing.extraLarge)
                        .shadow(
                            elevation = 2.dp,
                            shape = MaterialTheme.shapes.large
                        ),
                    shape = MaterialTheme.shapes.large,
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    AsyncImage(
                        model = state.articleDetail.imageUrl,
                        error = painterResource(R.drawable.ic_image_placeholder),
                        placeholder = painterResource(R.drawable.ic_image_placeholder),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.large),
                        contentScale = ContentScale.Crop
                    )
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = SpaceFlightNewsTheme.spacing.extraLarge)
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                        .shadow(
                            elevation = SpaceFlightNewsTheme.spacing.minDp,
                            shape = MaterialTheme.shapes.medium
                        ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)

                ) {
                    Text(
                        text = state.articleDetail.summary,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(SpaceFlightNewsTheme.spacing.medium),
                        textAlign = TextAlign.Justify
                    )
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = SpaceFlightNewsTheme.spacing.extraLarge)
                        .shadow(
                            elevation = SpaceFlightNewsTheme.spacing.minDp,
                            shape = MaterialTheme.shapes.medium
                        ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(
                            alpha = 0.5f
                        )
                    )
                ) {
                    Column(modifier = Modifier.padding(SpaceFlightNewsTheme.spacing.medium)) {
                        Text(
                            text = "By ${state.articleDetail.authors.joinToString(", ")}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 1f)
                        )
                        Spacer(modifier = Modifier.height(SpaceFlightNewsTheme.spacing.extraSmall))
                        Text(
                            text = "Published on ${state.articleDetail.publishedAt}, Updated ${state.articleDetail.updatedAt}",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = SpaceFlightNewsTheme.spacing.extraLarge)
                        .clickable {
                            openUrl(
                                context,
                                state.articleDetail.newsSiteUrl
                            )
                        }
                        .shadow(
                            elevation = 2.dp,
                            shape = MaterialTheme.shapes.medium
                        ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(
                                vertical = SpaceFlightNewsTheme.spacing.medium,
                                horizontal = SpaceFlightNewsTheme.spacing.large
                            )
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_rocket),
                            contentDescription = "Full Article Link",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier
                                .padding(bottom = SpaceFlightNewsTheme.spacing.small)
                                .height(SpaceFlightNewsTheme.spacing.large)
                                .width(SpaceFlightNewsTheme.spacing.large)
                        )
                        Text(
                            text = "Read Full Article at ${state.articleDetail.newsSiteUrl}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            ),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Spacer(modifier = Modifier.height(SpaceFlightNewsTheme.spacing.medium))
            }
        }
    }

    if (showErrorBottomSheet) {
        ErrorBottomSheet(
            isShowBottomSheet = true,
            title = stringResource(R.string.parameters_are_null),
            onButtonClick = {
                showErrorBottomSheet = false
                onNavigateBack()
            },
            onDismissRequest = {
                showErrorBottomSheet = false
                onNavigateBack()
            }
        )
    }
}

@Composable
fun FavoriteIcon() {
    Icon(
        imageVector = Icons.Filled.Favorite,
        contentDescription = "Favorite",
        tint = Color.Red,
        modifier = Modifier.padding(start = SpaceFlightNewsTheme.spacing.small)
    )
}

private fun shareArticle(context: Context, url: String) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, url)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}

private fun openUrl(context: Context, url: String) {
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(browserIntent)
}