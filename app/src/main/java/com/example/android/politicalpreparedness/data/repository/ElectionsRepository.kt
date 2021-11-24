package com.example.android.politicalpreparedness.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.politicalpreparedness.data.database.ElectionDao
import com.example.android.politicalpreparedness.data.network.CivicsApi
import com.example.android.politicalpreparedness.data.network.models.Election
import com.example.android.politicalpreparedness.data.network.models.VoterInfoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.properties.Delegates


class ElectionsRepository(private val electionDao: ElectionDao) {

    // Data exposed to the public - they don't have to care where do the data comes from
    val upcomingElections: LiveData<List<Election>> = electionDao.getAllElections()
    val followedElections: LiveData<List<Election>> = electionDao.getFollowedElections()

    private var _voterInfo: VoterInfoResponse? = null
    val voterInfo get() = _voterInfo

    private var _isFollowed by Delegates.notNull<Boolean>()
    val isFollowed get() = _isFollowed

    private var _voterInfoLoadError = MutableLiveData(false)
    val voterInfoLoadError: LiveData<Boolean>
        get() = _voterInfoLoadError

    /***
     * Fetch data from REST API and store to database
     */
    suspend fun fetchUpcomingElections() = withContext(Dispatchers.IO) {
        try {
            val electionResponse = CivicsApi.retrofitService.getElections()
            electionDao.insertAll(electionResponse.elections)
        } catch (e: Exception) {
            e.printStackTrace()
            // We just do not make changes to the DB if API failed.
        }
    }

    suspend fun isElectionFollowed(electionId: Int) = withContext(Dispatchers.IO) {
        _isFollowed = electionDao.isFollowedElection(electionId)
    }

    // ---- VoterInfo ----
    suspend fun fetchVoterInfo(electionId: Int, address: String) = withContext(Dispatchers.IO) {
        _voterInfo = try {
            _voterInfoLoadError.postValue(false)
            CivicsApi.retrofitService.getVoterInfo(address, electionId)
        } catch (e: Exception) {
            e.printStackTrace()
            _voterInfoLoadError.postValue(true)
            null
        }
    }

    suspend fun unfollowElection(electionId: Int) = withContext(Dispatchers.IO) {
        electionDao.unfollowElection(electionId)
    }


    suspend fun followElection(electionId: Int) = withContext(Dispatchers.IO) {
        electionDao.followElection(electionId)
    }
}