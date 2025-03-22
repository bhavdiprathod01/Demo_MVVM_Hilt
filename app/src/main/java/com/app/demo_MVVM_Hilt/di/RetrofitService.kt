package com.app.demo_MVVM_Hilt.di

import com.app.demo_MVVM_Hilt.BuildConfig
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
open class RetrofitService {

    private var gson: Gson

    private var BASE_URL = "https://login.socialgreetings.in/services/"

    init {
        gson = GsonBuilder()
            .setLenient()
            .create()
    }
    @Provides
    fun provideHttpClientForBaseApi(
    ): OkHttpClient {
        val httpClient = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            val interceptor =
                HttpLoggingInterceptor().also { it.setLevel(HttpLoggingInterceptor.Level.BODY) }
            httpClient.addInterceptor(interceptor).build()
        } else {
            val interceptor =
                HttpLoggingInterceptor().also { it.setLevel(HttpLoggingInterceptor.Level.NONE) }
            httpClient.addInterceptor(interceptor).build()
        }

        httpClient.addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .method(original.method, original.body)
                .header("Accept", "application/json")
                .build()
            chain.proceed(request)
        }
        httpClient.readTimeout(120, TimeUnit.SECONDS)
        httpClient.connectTimeout(120, TimeUnit.SECONDS)
        return httpClient.build()
    }


    @Singleton
    @Provides
    fun baseApiServices(
        okHttpClient: OkHttpClient
    ): ApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(ApiService::class.java)
}