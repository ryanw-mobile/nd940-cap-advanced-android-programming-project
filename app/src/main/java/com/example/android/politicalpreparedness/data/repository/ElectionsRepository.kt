package com.example.android.politicalpreparedness.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.politicalpreparedness.data.database.ElectionDao
import com.example.android.politicalpreparedness.data.network.CivicsApi
import com.example.android.politicalpreparedness.data.network.models.Election
import com.example.android.politicalpreparedness.data.network.models.VoterInfoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.properties.Delegates

class ElectionsRepository @Inject constructor(private val electionDao: ElectionDao) :
    Repository {

    // Data exposed to the public - they don't have to care where do the data comes from
    override val upcomingElections: LiveData<List<Election>> = electionDao.observeElectionList()
    override val followedElections: LiveData<List<Election>> = electionDao.observeFollowedElections()

    private var _voterInfo: VoterInfoResponse? = null
    override val voterInfo get() = _voterInfo

    private var _isFollowed by Delegates.notNull<Boolean>()
    override val isFollowed get() = _isFollowed

    private var _voterInfoLoadError = MutableLiveData(false)
    override val voterInfoLoadError: LiveData<Boolean>
        get() = _voterInfoLoadError

    /***
     * Fetch data from REST API and store to database
     */
    override suspend fun fetchUpcomingElections() = withContext(Dispatchers.IO) {
        try {
            val electionResponse = CivicsApi.retrofitService.getElections()
            electionDao.insertAll(electionResponse.elections)
        } catch (e: Exception) {
            e.printStackTrace()
            // We just do not make changes to the DB if API failed.
        }
    }

    override suspend fun isElectionFollowed(electionId: Int) = withContext(Dispatchers.IO) {
        _isFollowed = electionDao.isFollowedElection(electionId)
    }

    // ---- VoterInfo ----
    override suspend fun fetchVoterInfo(electionId: Int, address: String) = withContext(Dispatchers.IO) {
        _voterInfo = try {
            _voterInfoLoadError.postValue(false)
            CivicsApi.retrofitService.getVoterInfo(address, electionId)
        } catch (e: Exception) {
            e.printStackTrace()
            _voterInfoLoadError.postValue(true)
            null
        }
    }

    override suspend fun unfollowElection(electionId: Int) = withContext(Dispatchers.IO) {
        electionDao.unfollowElection(electionId)
    }


    override suspend fun followElection(electionId: Int) = withContext(Dispatchers.IO) {
        electionDao.followElection(electionId)
    }
}