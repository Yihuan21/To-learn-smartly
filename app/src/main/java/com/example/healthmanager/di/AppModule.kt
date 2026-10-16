package com.example.healthmanager.di

import android.content.Context
import androidx.room.Room
import com.example.healthmanager.data.local.AppDatabase
import com.example.healthmanager.data.remote.LlmApiService
import com.example.healthmanager.domain.HealthAnalysis
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    const val BASE_URL = "https://api.openai.com/v1/"

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "health_local.db").build()

    @Provides
    @Singleton
    fun provideMoshi(): Moshi =
        Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    @Provides
    @Singleton
    fun provideRetrofit(moshi: Moshi): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides
    @Singleton
    fun provideLlmApi(retrofit: Retrofit): LlmApiService =
        retrofit.create(LlmApiService::class.java)

    @Provides
    @Singleton
    fun provideAnalysisAdapter(moshi: Moshi): JsonAdapter<HealthAnalysis> =
        moshi.adapter(HealthAnalysis::class.java)
}
