package com.example.android.politicalpreparedness.data.repository

import androidx.lifecycle.LiveData
import com.example.android.politicalpreparedness.data.network.models.Election
import com.example.android.politicalpreparedness.data.network.models.VoterInfoResponse

interface Repository {
    // Data exposed to the public - they don't have to care where do the data comes from
    val upcomingElections: LiveData<List<Election>>
    val followedElections: LiveData<List<Election>>
    val voterInfo: VoterInfoResponse?
    val isFollowed: Boolean
    val voterInfoLoadError: LiveData<Boolean>

    /***
     * Fetch data from REST API and store to database
     */
    suspend fun fetchUpcomingElections()

    suspend fun isElectionFollowed(electionId: Int)

    // ---- VoterInfo ----
    suspend fun fetchVoterInfo(electionId: Int, address: String)

    suspend fun unfollowElection(electionId: Int)

    suspend fun followElection(electionId: Int)
}