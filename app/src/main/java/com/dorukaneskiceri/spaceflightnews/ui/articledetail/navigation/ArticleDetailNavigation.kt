package com.dorukaneskiceri.spaceflightnews.ui.articledetail.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.dorukaneskiceri.spaceflightnews.ui.articledetail.ArticleDetailScreen
import com.dorukaneskiceri.spaceflightnews.ui.articledetail.model.ArticleDetailNavigationModel
import com.dorukaneskiceri.spaceflightnews.util.getNavigationArgument

fun NavController.navigateToArticleDetailScreen(
    navOptions: NavOptions? = null,
    articleDetail: ArticleDetailNavigationModel,
) {
    this.navigate(articleDetail)
}

fun NavGraphBuilder.articleDetailScreen(
    onNavigateBack: () -> Unit,
) {
    composable<ArticleDetailNavigationModel> {
        ArticleDetailScreen(
            onNavigateBack = onNavigateBack,
        )
    }
}