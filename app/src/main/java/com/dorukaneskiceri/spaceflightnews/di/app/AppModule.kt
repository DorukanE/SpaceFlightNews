package com.dorukaneskiceri.spaceflightnews.di.app

import com.dorukaneskiceri.spaceflightnews.di.network.BaseUrl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    @BaseUrl
    fun provideBaseUrl() = "https://api.spaceflightnewsapi.net/v4/" //Could get from BuildConfig
}