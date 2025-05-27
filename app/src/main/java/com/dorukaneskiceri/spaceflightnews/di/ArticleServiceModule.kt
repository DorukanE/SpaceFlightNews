package com.dorukaneskiceri.spaceflightnews.di

import com.dorukaneskiceri.spaceflightnews.data.ArticlesService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ArticleServiceModule {
    @Provides
    @Singleton
    fun provideArticlesService(retrofit: Retrofit): ArticlesService =
        retrofit.create(ArticlesService::class.java)
}
