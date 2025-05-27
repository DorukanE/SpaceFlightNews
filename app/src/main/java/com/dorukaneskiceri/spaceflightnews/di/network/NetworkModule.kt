package com.dorukaneskiceri.spaceflightnews.di.network

import android.content.Context
import com.dorukaneskiceri.spaceflightnews.util.BaseResult
import com.dorukaneskiceri.spaceflightnews.util.BaseResultTypeAdapter
import com.dorukaneskiceri.spaceflightnews.util.NetworkHandler
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

private const val CONNECT_TIMEOUT_SEC = 10L
private const val DATA_TIMEOUT_SEC = 30L

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(BaseResult::class.java, BaseResultTypeAdapter())
            .create()
    }

    @Singleton
    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = Level.BODY
    }

    @Provides
    @Singleton
    fun provideHttpClient(
        interceptor: HttpLoggingInterceptor,
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
        builder.connectTimeout(CONNECT_TIMEOUT_SEC, TimeUnit.SECONDS)
        builder.readTimeout(DATA_TIMEOUT_SEC, TimeUnit.SECONDS)
        builder.writeTimeout(DATA_TIMEOUT_SEC, TimeUnit.SECONDS)
        builder.addInterceptor(interceptor)
        return builder.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        @BaseUrl baseUrl: String,
        client: OkHttpClient,
        gson: Gson
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    @Singleton
    @Provides
    fun provideNetworkHandler(@ApplicationContext context: Context) =
        NetworkHandler(context)
}
