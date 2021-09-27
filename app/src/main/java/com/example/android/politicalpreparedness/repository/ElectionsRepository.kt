package com.example.android.politicalpreparedness.network.repository

import androidx.lifecycle.LiveData
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class ElectionsRepository(private val database: ElectionDatabase) {

    // Data exposed to the public - they don't have to care where do the data comes from
    val upcomingElections: LiveData<List<Election>> = database.electionDao.getAllElections()
    val followedElections: LiveData<List<Election>> = database.electionDao.getFollowedElections()

    var voterInfo: VoterInfoResponse? = null

    /***
     * Fetch data from REST API and store to database
     */
    suspend fun fetchUpcomingElections() {
        withContext(Dispatchers.IO) {
            try {
                val getElectionsDeferred = CivicsApi.retrofitService.getElections()
                database.electionDao.insertAll(getElectionsDeferred.elections)
            } catch (e: Exception) {
                e.printStackTrace()
                // We just do not make changes to the DB if API failed.
            }
        }
    }

    // ---- VoterInfo ----
    suspend fun fetchVoterInfo(electionId: Int, address: String) {
        withContext(Dispatchers.IO) {
            voterInfo = try {
                CivicsApi.retrofitService.getVoterInfo(address, electionId)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

}