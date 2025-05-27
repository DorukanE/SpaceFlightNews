package com.dorukaneskiceri.spaceflightnews.data

import com.dorukaneskiceri.spaceflightnews.data.local.dao.ArticleDao
import com.dorukaneskiceri.spaceflightnews.data.local.db.entity.ArticleEntity
import com.dorukaneskiceri.spaceflightnews.data.local.db.entity.ArticleResponseEntity
import com.dorukaneskiceri.spaceflightnews.data.local.db.entity.AuthorEntity
import com.dorukaneskiceri.spaceflightnews.data.local.db.entity.FavoriteArticleEntity
import com.dorukaneskiceri.spaceflightnews.data.model.toDomainModel
import com.dorukaneskiceri.spaceflightnews.domain.model.Article
import com.dorukaneskiceri.spaceflightnews.domain.model.ArticleList
import com.dorukaneskiceri.spaceflightnews.domain.model.Author
import com.dorukaneskiceri.spaceflightnews.domain.repository.ArticleRepository
import com.dorukaneskiceri.spaceflightnews.util.BaseResult
import com.dorukaneskiceri.spaceflightnews.util.NetworkHandler
import com.dorukaneskiceri.spaceflightnews.util.apiCall
import com.dorukaneskiceri.spaceflightnews.util.ext.onTransform
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ArticleRepositoryImpl @Inject constructor(
    private val articlesService: ArticlesService,
    private val networkHandler: NetworkHandler,
    private val articleDao: ArticleDao,
) : ArticleRepository {

    override val gson: Gson
        get() = super.gson

    override fun getArticles(
        search: String?,
        limit: Int?,
        offset: Int?
    ): Flow<BaseResult<Article>> {
        return apiCall(networkHandler) {
            articlesService.getArticles(search, limit, offset).onTransform {
                it.toDomainModel()
            }
        }
    }

    override suspend fun saveArticleResponseToDatabase(articleResponse: Article) {
        try {
            val articleResponseEntity = ArticleResponseEntity(
                count = articleResponse.count,
                next = articleResponse.next,
                previous = articleResponse.previous
            )
            val articleResponseId = articleDao.insertArticleResponse(articleResponseEntity)

            val favoriteArticleIds = articleDao.getFavoriteArticles().map { it.articleId }

            articleResponse.results.forEach { article ->
                val isFavorite = favoriteArticleIds.contains(article.id)

                val articleEntity = ArticleEntity(
                    id = article.id,
                    articleResponseId = articleResponseId,
                    title = article.title,
                    url = article.url,
                    imageUrl = article.imageUrl,
                    newsSite = article.newsSite,
                    summary = article.summary,
                    publishedAt = article.publishedAt,
                    updatedAt = article.updatedAt,
                    featured = article.featured,
                    isFavorite = isFavorite
                )
                articleDao.insertArticle(articleEntity)

                article.authors.forEach { author ->
                    val authorEntity = AuthorEntity(
                        articleListId = article.id,
                        name = author.name,
                    )
                    articleDao.insertAuthor(authorEntity)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun getArticleResponseFromDatabase(): Flow<Article> {
        try {
            val articleResponseEntity = articleDao.getArticleResponse()
            articleResponseEntity.let { articleResponse ->
                val articleEntities = articleDao.getArticles()
                val articleList = articleEntities.map { articleEntity ->
                    ArticleList(
                        id = articleEntity.id,
                        title = articleEntity.title,
                        authors = getAuthorsFromDatabase(articleEntity.id),
                        url = articleEntity.url,
                        imageUrl = articleEntity.imageUrl,
                        newsSite = articleEntity.newsSite,
                        summary = articleEntity.summary,
                        publishedAt = articleEntity.publishedAt,
                        updatedAt = articleEntity.updatedAt,
                        featured = articleEntity.featured,
                        isFavorite = articleEntity.isFavorite
                    )
                }
                return flow {
                    emit(
                        Article(
                            count = articleResponse.count,
                            next = articleResponse.next,
                            previous = articleResponse.previous,
                            results = articleList
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return flow {
                emit(Article(count = 0, next = null, previous = null, results = emptyList()))
            }
        }
    }

    override suspend fun updateArticleFavoriteStatus(articleId: Int, isFavorite: Boolean) {
        try {
            if (isFavorite) {
                articleDao.insertFavoriteArticle(FavoriteArticleEntity(articleId))
            } else {
                articleDao.deleteFavoriteArticle(articleId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun getFavoriteArticles(): Flow<List<ArticleList>> {
        return flow {
            try {
                val favoriteArticleIds = articleDao.getFavoriteArticles().map { it.articleId }
                val articleEntities = articleDao.getArticles().filter { favoriteArticleIds.contains(it.id) }

                emit(articleEntities.map { articleEntity ->
                    ArticleList(
                        id = articleEntity.id,
                        title = articleEntity.title,
                        authors = getAuthorsFromDatabase(articleEntity.id),
                        url = articleEntity.url,
                        imageUrl = articleEntity.imageUrl,
                        newsSite = articleEntity.newsSite,
                        summary = articleEntity.summary,
                        publishedAt = articleEntity.publishedAt,
                        updatedAt = articleEntity.updatedAt,
                        featured = articleEntity.featured,
                        isFavorite = true
                    )
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun getAuthorsFromDatabase(articleId: Int): List<Author> {
        val authorEntities = articleDao.getAuthors().filter { it.articleListId == articleId }
        return authorEntities.map { authorEntity ->
            Author(
                name = authorEntity.name,
            )
        }
    }
}