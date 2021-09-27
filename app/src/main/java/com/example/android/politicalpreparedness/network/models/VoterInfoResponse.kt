package com.example.android.politicalpreparedness.network.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class VoterInfoResponse(
    val election: Election,
    val pollingLocations: String? = null, //IGNORED: Future Use
    val contests: String? = null, //IGNORED: Future Use
    val state: List<State>? = null,
    val electionElectionOfficials: List<ElectionOfficial>? = null
)