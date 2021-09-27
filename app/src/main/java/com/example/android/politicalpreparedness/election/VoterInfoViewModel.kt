package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.network.repository.ElectionsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class VoterInfoViewModel(
    private val repository: ElectionsRepository,
    private val electionId: Int,
    private val division: Division
) :
    ViewModel() {

    //COMPLETED: Add live data to hold voter info
    private var _voterInfo = MutableLiveData<VoterInfoResponse>()
    val voterInfo: LiveData<VoterInfoResponse>
        get() = _voterInfo

    // TextView clickable to open a link here
    private val _locationUrl = MutableLiveData<String>()
    val locationUrl: LiveData<String> = _locationUrl

    // TextView clickable to open a link here
    private val _ballotUrl = MutableLiveData<String>()
    val ballotUrl: LiveData<String> = _ballotUrl

    private val _address = MutableLiveData<String>()
    val address: LiveData<String> = _address

    //TODO: Add var and methods to support loading URLs

    private var _isFollowed = MutableLiveData<Boolean>()
    val isFollowed: LiveData<Boolean>
        get() = _isFollowed

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */
    init {
        coroutineScope.launch {
            //COMPLETED: cont'd -- Populate initial state of save button to reflect proper action based on election saved status
            getElectionFollowStatus()

            //COMPLETED: Add var and methods to populate voter info
            // Given we have the electionId, the address can be rather casual
            val country = division.country
            val state = division.state
            val address = "$state, $country"
            repository.fetchVoterInfo(electionId, address)
            _voterInfo.value = repository.voterInfo
        }
    }

    private fun getElectionFollowStatus() {
        coroutineScope.launch {
            repository.isElectionFollowed(electionId)
            _isFollowed.value = repository.isFollowed
        }
    }

    //COMPLETED: Add var and methods to save and remove elections to local database
    fun toggleFollowElection() {
        coroutineScope.launch {
            if (_isFollowed.value == true) {
                repository.unfollowElection(electionId)
                _isFollowed.value = false
            } else {
                repository.followElection(electionId)
                _isFollowed.value = true
            }
        }
    }

}