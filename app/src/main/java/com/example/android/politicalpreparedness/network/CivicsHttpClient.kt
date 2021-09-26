package com.example.android.politicalpreparedness.network

import mykeys
import okhttp3.OkHttpClient

class CivicsHttpClient : OkHttpClient() {

    companion object {

        const val API_KEY = mykeys.KEY_CIVICAPI //COMPLETED: Place your API Key Here

        fun getClient(): OkHttpClient {
            return Builder()
                .addInterceptor { chain ->
                    val original = chain.request()
                    val url = original
                        .url()
                        .newBuilder()
                        .addQueryParameter("key", API_KEY)
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