package com.example.android.politicalpreparedness.ui.destination.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.data.network.models.Election
import com.example.android.politicalpreparedness.data.repository.ElectionsRepository
import com.example.android.politicalpreparedness.di.DispatcherModule
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

@HiltViewModel
class ElectionsViewModel @Inject constructor(
    private val repository: ElectionsRepository,
    @DispatcherModule.MainDispatcher private val dispatcher: CoroutineDispatcher,
) : ViewModel() {
    val upcomingElections = repository.upcomingElections
    val savedElections = repository.followedElections

    private var _selectedElection = MutableLiveData<Election?>()
    val selectedElection: LiveData<Election?>
        get() = _selectedElection

    init {
        viewModelScope.launch(dispatcher) {
            _selectedElection.value = null
            repository.fetchUpcomingElections()
        }
    }

    fun navigateToVoterInfo(election: Election) {
        _selectedElection.value = election
    }

    fun doneNavigateToVoterInfo() {
        _selectedElection.value = null
    }
}
