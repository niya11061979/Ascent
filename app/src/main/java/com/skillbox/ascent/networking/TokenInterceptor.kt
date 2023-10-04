package com.skillbox.ascent.networking

import com.skillbox.ascent.data.auth.AuthConfig
import com.skillbox.ascent.data.token.RefreshToken
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        return run {
            val modifiedRequest = originalRequest.newBuilder()
                .addHeader("client_id", AuthConfig.CLIENT_ID)
                .addHeader("client_secret", AuthConfig.CLIENT_SECRET)
                .addHeader("grant_type", AuthConfig.REFRESH_TOKEN)
                .addHeader("refresh_token", RefreshToken.token)
                .build()
            chain.proceed(modifiedRequest)
        }
    }
}