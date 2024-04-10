package com.spike.mdocmocklibrary.api

import android.content.Context
import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(context: Context, accessToken: String?) : Interceptor {

    val authToken = accessToken //Pass accessToken

    override fun intercept(chain: Interceptor.Chain): Response {

        val token = authToken
        val request = chain.request()
        if (!token.isNullOrEmpty()) {
            val newRequest = request
                .newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
            return chain.proceed(newRequest)
        }
        return chain.proceed(request)
    }
}