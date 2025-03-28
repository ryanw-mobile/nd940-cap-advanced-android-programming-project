package com.example.android.politicalpreparedness.data.network

import com.example.android.politicalpreparedness.data.network.models.ElectionResponse
import com.example.android.politicalpreparedness.data.network.models.RepresentativeResponse
import com.example.android.politicalpreparedness.data.network.models.VoterInfoResponse
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.Date

private const val BASE_URL = "https://www.googleapis.com/civicinfo/v2/"

/**
 * Moshi's composition mechanism tries to find the best adapter for each type.
 * It starts with the first adapter or factory registered with Moshi.Builder.add(),
 * and proceeds until it finds an adapter for the target type.
 * If a type can be matched multiple adapters, the earliest one wins.
 * To register an adapter at the end of the list, use Moshi.Builder.addLast() instead.
 * This is most useful when registering general-purpose adapters, such as the KotlinJsonAdapterFactory below.
 */
private val moshi =
    Moshi.Builder()
        .add(ElectionJsonAdapter())
        .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
        .add(KotlinJsonAdapterFactory())
        .build()

private val retrofit =
    Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .client(CivicsHttpClient.getClient())
        .baseUrl(BASE_URL)
        .build()

/**
 *  Documentation for the Google Civics API Service can be found at https://developers.google.com/civic-information/docs/v2
 */

sealed interface CivicsApiService {
    // COMPLETED: Add elections API Call
    @GET("elections")
    suspend fun getElections(): ElectionResponse

    // COMPLETED: Add voterinfo API Call
    @GET("voterinfo")
    suspend fun getVoterInfo(
        @Query("address")
        address: String,
        @Query("electionId")
        electionId: Int,
    ): VoterInfoResponse

    // COMPLETED: Add representatives API Call
    // No need to call await() from inside the Coroutine
    // Therefore using suspend here without a need to use Deferred<...> as return type
    @GET("representatives")
    suspend fun getRepresentatives(
        @Query("address")
        address: String,
    ): RepresentativeResponse
}

object CivicsApi {
    val retrofitService: CivicsApiService by lazy {
        retrofit.create(CivicsApiService::class.java)
    }
}
