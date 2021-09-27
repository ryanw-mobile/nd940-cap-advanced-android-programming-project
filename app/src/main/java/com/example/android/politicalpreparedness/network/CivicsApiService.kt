package com.example.android.politicalpreparedness.network

import com.example.android.politicalpreparedness.network.jsonadapter.ElectionAdapter
import com.example.android.politicalpreparedness.network.models.ElectionResponse
import com.example.android.politicalpreparedness.network.models.RepresentativeResponse
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://www.googleapis.com/civicinfo/v2/"

// COMPLETED: Add adapters for Java Date and custom adapter ElectionAdapter (included in project)
/**
 * Moshi's composition mechanism tries to find the best adapter for each type.
 * It starts with the first adapter or factory registered with Moshi.Builder.add(),
 * and proceeds until it finds an adapter for the target type.
 * If a type can be matched multiple adapters, the earliest one wins.
 * To register an adapter at the end of the list, use Moshi.Builder.addLast() instead.
 * This is most useful when registering general-purpose adapters, such as the KotlinJsonAdapterFactory below.
 */
private val moshi = Moshi.Builder()
    .add(ElectionAdapter())
    .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .client(CivicsHttpClient.getClient())
    .baseUrl(BASE_URL)
    .build()

/**
 *  Documentation for the Google Civics API Service can be found at https://developers.google.com/civic-information/docs/v2
 */

interface CivicsApiService {
    //COMPLETED: Add elections API Call
    @GET("elections")
    suspend fun getElections(): ElectionResponse

    //COMPLETED: Add voterinfo API Call
    @GET("voterinfo")
    suspend fun getVoterInfo(
        @Query("electionId")
        electionId: Int,
        @Query("address")
        address: String
    ): VoterInfoResponse

    //COMPLETED: Add representatives API Call
    @GET("representatives")
    suspend fun getRepresentatives(
        @Query("address")
        address: String
    ): Deferred<RepresentativeResponse>
}

object CivicsApi {
    val retrofitService: CivicsApiService by lazy {
        retrofit.create(CivicsApiService::class.java)
    }
}