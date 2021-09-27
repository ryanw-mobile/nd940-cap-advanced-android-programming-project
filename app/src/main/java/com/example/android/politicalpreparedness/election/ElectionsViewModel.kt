package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

//COMPLETED: Construct ViewModel and provide election datasource
class ElectionsViewModel(private val dataSource: ElectionDao) : ViewModel() {

    //COMPLETED: Create live data val for upcoming elections
    private val _upcomingElections = MutableLiveData<List<Election>>()
    val upcoomingElections: LiveData<List<Election>>
        get() = _upcomingElections

    //TODO: Create live data val for saved elections

    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database
    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)
    fun fetchUpcomingElections() {
        coroutineScope.launch {
            try {
                val getElectionsDeferred = CivicsApi.retrofitService.getElections()
                _upcomingElections.value = getElectionsDeferred.elections
            } catch (e: Exception) {
                e.printStackTrace()
                _upcomingElections.value = emptyList()
            }
        }
    }

    //TODO: Create functions to navigate to saved or upcoming election voter info

}