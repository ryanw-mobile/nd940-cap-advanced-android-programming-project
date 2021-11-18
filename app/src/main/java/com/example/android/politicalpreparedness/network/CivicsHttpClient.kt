package com.example.android.politicalpreparedness.network

import MyKeys
import okhttp3.OkHttpClient

class CivicsHttpClient : OkHttpClient() {

    companion object {

        fun getClient(): OkHttpClient {
            return Builder()
                .addInterceptor { chain ->
                    val original = chain.request()
                    val url = original
                        .url()
                        .newBuilder()
                        .addQueryParameter("key", MyKeys.KEY_CIVICAPI)
                        .build()
                    val request = original
                        .newBuilder()
                        .url(url)
                        .build()
                    chain.proceed(request)
                }
                .build()
        }

    }

}