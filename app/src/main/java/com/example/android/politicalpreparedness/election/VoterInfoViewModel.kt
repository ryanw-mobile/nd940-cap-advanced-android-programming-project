package com.example.android.politicalpreparedness.election

import androidx.databinding.BaseObservable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.politicalpreparedness.database.ElectionDao

class VoterInfoViewModel(private val dataSource: ElectionDao) : BaseObservable() {

    //TODO: Add live data to hold voter info
    // These have been bind to the XML layout
    private val _electionTitle = MutableLiveData<String>()
    val electionTitle: LiveData<String> = _electionTitle

    private val _electionDate = MutableLiveData<String>()
    val electionDate: LiveData<String> = _electionDate

    // TextView clickable to open a link here
    private val _locationUrl = MutableLiveData<String>()
    val locationUrl: LiveData<String> = _locationUrl

    // TextView clickable to open a link here
    private val _ballotUrl = MutableLiveData<String>()
    val ballotUrl: LiveData<String> = _ballotUrl

    private val _address = MutableLiveData<String>()
    val address: LiveData<String> = _address

    //TODO: Add var and methods to populate voter info

    //TODO: Add var and methods to support loading URLs

    //TODO: Add var and methods to save and remove elections to local database
    //TODO: cont'd -- Populate initial state of save button to reflect proper action based on election saved status

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */

    fun toggleFollowElection() {
        // TODO: toggle follow state
    }
}