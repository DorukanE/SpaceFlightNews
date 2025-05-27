package com.dorukaneskiceri.spaceflightnews.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.dorukaneskiceri.spaceflightnews.ui.article.navigation.articleScreen
import com.dorukaneskiceri.spaceflightnews.ui.article.navigation.articleScreenRoute
import com.dorukaneskiceri.spaceflightnews.ui.articledetail.navigation.articleDetailScreen
import com.dorukaneskiceri.spaceflightnews.ui.articledetail.navigation.navigateToArticleDetailScreen

@Composable
fun SpaceFlightNewsApp(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = articleScreenRoute
    ) {
        articleScreen(
            onGoToDetailScreen = {
                navController.navigateToArticleDetailScreen(articleDetail = it)
            }
        )
        articleDetailScreen(
            onNavigateBack = {
                navController.navigateUp()
            }
        )
    }
}