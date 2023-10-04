package com.skillbox.ascent.networking

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

object Networking {
    private val okHttpClient = OkHttpClient.Builder()
        .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .addInterceptor(CustomInterceptor())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://www.strava.com/")
        .addConverterFactory(MoshiConverterFactory.create())
        .client(okHttpClient)
        .build()

    val stravaApi: StravaApi
        get() = retrofit.create()

    //------------для запроса обновленя токена---------------------------------------------------
    private val okHttpRefresh = OkHttpClient.Builder()
        .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .addInterceptor(TokenInterceptor())
        .build()

    private val retrofitRefreshToken = Retrofit.Builder()
        .baseUrl("https://www.strava.com/")
        .addConverterFactory(MoshiConverterFactory.create())
        .client(okHttpRefresh)
        .build()

    val authApi: AuthApi
        get() = retrofitRefreshToken.create()
}