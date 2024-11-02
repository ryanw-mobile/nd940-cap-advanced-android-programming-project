package com.example.android.politicalpreparedness.data.network.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class VoterInfoResponse(
    val election: Election,
    // IGNORED: Future Use
    val pollingLocations: String? = null,
    // IGNORED: Future Use
    val contests: String? = null,
    val state: List<State>? = null,
    val electionElectionOfficials: List<ElectionOfficial>? = null,
)
