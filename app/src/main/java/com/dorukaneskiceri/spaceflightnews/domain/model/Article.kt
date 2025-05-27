package com.dorukaneskiceri.spaceflightnews.domain.model

data class Article(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<ArticleList>
)
