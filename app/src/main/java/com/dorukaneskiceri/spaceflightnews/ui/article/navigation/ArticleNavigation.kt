package com.dorukaneskiceri.spaceflightnews.ui.article.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.dorukaneskiceri.spaceflightnews.ui.article.screen.ArticleScreen
import com.dorukaneskiceri.spaceflightnews.ui.articledetail.model.ArticleDetailNavigationModel

const val articleScreenRoute = "article_screen_route"

fun NavController.navigateToArticleScreen(
    navOptions: NavOptions? = null,
) {
    this.navigate(route = articleScreenRoute, navOptions = navOptions)
}

fun NavGraphBuilder.articleScreen(
    onGoToDetailScreen: (ArticleDetailNavigationModel) -> Unit,
) {
    composable(
        route = articleScreenRoute,
    ) {
        ArticleScreen(onGoToDetailScreen = onGoToDetailScreen)
    }
}