package com.skillbox.ascent.networking

import com.skillbox.ascent.data.token.AccessToken
import okhttp3.Interceptor
import okhttp3.Response

class CustomInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        return run {
            val modifiedRequest = originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer " + AccessToken.token)
                .build()
            chain.proceed(modifiedRequest)
        }
    }
}