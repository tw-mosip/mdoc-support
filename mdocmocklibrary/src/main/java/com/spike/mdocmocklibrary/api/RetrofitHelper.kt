package com.spike.mdocmocklibrary.api

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitHelper {


    fun getInstance(context: Context, accessToken: String? = null): Retrofit {
        val authInterceptor = AuthInterceptor(context, accessToken)
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }
}