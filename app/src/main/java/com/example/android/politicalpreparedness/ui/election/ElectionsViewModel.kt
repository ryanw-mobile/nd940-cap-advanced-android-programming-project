package com.example.android.politicalpreparedness.ui.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.politicalpreparedness.data.network.models.Election
import com.example.android.politicalpreparedness.data.repository.ElectionsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

//COMPLETED: Construct ViewModel and provide election datasource
@HiltViewModel
class ElectionsViewModel @Inject constructor(private val repository: ElectionsRepository) :
    ViewModel() {

    //COMPLETED: Create live data val for upcoming elections
    val upcomingElections = repository.upcomingElections

    //COMPLETED: Create live data val for saved elections
    val savedElections = repository.followedElections

    private var _selectedElection = MutableLiveData<Election?>()
    val selectedElection: LiveData<Election?>
        get() = _selectedElection

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        coroutineScope.launch {
            _selectedElection.value = null
            repository.fetchUpcomingElections()
        }
    }

    //COMPLETED: Create functions to navigate to saved or upcoming election voter info
    fun navigateToVoterInfo(election: Election) {
        _selectedElection.value = election
    }

    fun doneNavigateToVoterInfo() {
        _selectedElection.value = null
    }

}