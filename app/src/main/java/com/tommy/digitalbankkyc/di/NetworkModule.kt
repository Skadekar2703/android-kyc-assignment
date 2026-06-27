package com.tommy.digitalbankkyc.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.tommy.digitalbankkyc.data.remote.api.DummyJsonApiService
import com.tommy.digitalbankkyc.data.remote.api.IfscApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    @Named("dummyJson")
    fun provideDummyJsonRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://dummyjson.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    @Named("ifsc")
    fun provideIfscRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://ifsc.razorpay.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideDummyJsonApiService(
        @Named("dummyJson") retrofit: Retrofit
    ): DummyJsonApiService = retrofit.create(DummyJsonApiService::class.java)

    @Provides
    @Singleton
    fun provideIfscApiService(
        @Named("ifsc") retrofit: Retrofit
    ): IfscApiService = retrofit.create(IfscApiService::class.java)
}
