package com.example.android.politicalpreparedness.data.network

import com.example.android.politicalpreparedness.data.network.models.Division
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

class ElectionJsonAdapter {
    @FromJson
    fun divisionFromJson(ocdDivisionId: String): Division {
        val countryDelimiter = "country:"
        val stateDelimiter = "state:"
        val country =
            ocdDivisionId.substringAfter(countryDelimiter, "")
                .substringBefore("/")
        val state =
            ocdDivisionId.substringAfter(stateDelimiter, "")
                .substringBefore("/")
        return Division(ocdDivisionId, country, state)
    }

    @ToJson
    fun divisionToJson(division: Division): String {
        return division.id
    }
}
