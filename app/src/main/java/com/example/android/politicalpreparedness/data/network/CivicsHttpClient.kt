package com.example.android.politicalpreparedness.data.network

import com.example.android.politicalpreparedness.BuildConfig
import okhttp3.OkHttpClient

sealed class CivicsHttpClient : OkHttpClient() {
    companion object {
        fun getClient(): OkHttpClient = Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val url = original.url.newBuilder()
                    .addQueryParameter(
                        name = "key",
                        value = BuildConfig.CIVIC_API_KEY,
                    )
                    .build()
                val request =
                    original
                        .newBuilder()
                        .url(url)
                        .build()
                chain.proceed(request)
            }
            .build()
    }
}
