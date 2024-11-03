package com.example.android.politicalpreparedness.ui.destination.voterInfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.data.network.models.Division
import com.example.android.politicalpreparedness.data.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.data.repository.ElectionsRepository
import com.example.android.politicalpreparedness.di.DispatcherModule
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

@HiltViewModel
class VoterInfoViewModel @Inject constructor(
    private val repository: ElectionsRepository,
    private val savedStateHandle: SavedStateHandle,
    @DispatcherModule.MainDispatcher private val dispatcher: CoroutineDispatcher,
) : ViewModel() {
    private val electionId: Int = savedStateHandle["arg_election_id"]!!
    private val division: Division = savedStateHandle["arg_division"]!!

    // COMPLETED: Add live data to hold voter info
    private var _voterInfo = MutableLiveData<VoterInfoResponse>()
    val voterInfo: LiveData<VoterInfoResponse>
        get() = _voterInfo

    val voterInfoLoadError = repository.voterInfoLoadError

    // TextView clickable to open a link here
    private var _locationUrl = MutableLiveData<String>()
    val locationUrl: LiveData<String> = _locationUrl

    // TextView clickable to open a link here
    private var _ballotUrl = MutableLiveData<String>()
    val ballotUrl: LiveData<String> = _ballotUrl

    private var _address = MutableLiveData<String>()
    val address: LiveData<String> = _address

    private var _isFollowed = MutableLiveData<Boolean>()
    val isFollowed: LiveData<Boolean>
        get() = _isFollowed

    private var _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

//    private var viewModelJob = Job()
//    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */
    init {
        refresh()
    }

    private fun refresh() {
        viewModelScope.launch(dispatcher) {
            _isLoading.postValue(true)
            // COMPLETED: cont'd -- Populate initial state of save button to reflect proper action based on election saved status
            getElectionFollowStatus()

            // COMPLETED: Add var and methods to populate voter info
            // Given we have the electionId, the address can be rather casual
            repository.fetchVoterInfo(electionId, "${division.state}, ${division.country}")
            _voterInfo.value = repository.voterInfo
            _isLoading.postValue(false)
        }
    }

    private fun getElectionFollowStatus() {
        viewModelScope.launch(dispatcher) {
            repository.isElectionFollowed(electionId)
            _isFollowed.value = repository.isFollowed
        }
    }

    // COMPLETED: Add var and methods to save and remove elections to local database
    fun toggleFollowElection() {
        viewModelScope.launch(dispatcher) {
            if (_isFollowed.value == true) {
                repository.unfollowElection(electionId)
                _isFollowed.value = false
            } else {
                repository.followElection(electionId)
                _isFollowed.value = true
            }
        }
    }

    // COMPLETED: Add var and methods to support loading URLs
    fun getVotingLocationUrl(): String? = _voterInfo.value?.state?.get(0)?.electionAdministrationBody?.votingLocationFinderUrl

    fun getBallotInformationUrl(): String? = _voterInfo.value?.state?.get(0)?.electionAdministrationBody?.ballotInfoUrl

    fun getCorrespondenceAddress(): String? =
        _voterInfo.value?.state?.get(0)?.electionAdministrationBody?.correspondenceAddress?.toFormattedString()
}
