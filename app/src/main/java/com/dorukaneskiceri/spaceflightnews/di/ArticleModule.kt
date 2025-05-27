package com.dorukaneskiceri.spaceflightnews.di

import com.dorukaneskiceri.spaceflightnews.data.ArticleRepositoryImpl
import com.dorukaneskiceri.spaceflightnews.domain.repository.ArticleRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ArticleModule {

    @Binds
    @Singleton
    fun provideArticleRepository(
        articleRepositoryImpl: ArticleRepositoryImpl,
    ): ArticleRepository

}