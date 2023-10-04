package com.skillbox.ascent.networking

import com.skillbox.ascent.data.token.TokenInformation
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApi {
    @POST("api/v3/oauth/token")
    suspend fun refreshToken(): Call<TokenInformation>

    @POST("api/v3/oauth/deauthorize")
    suspend fun deauthorization(@Query("access_token") accessToken: String)
}