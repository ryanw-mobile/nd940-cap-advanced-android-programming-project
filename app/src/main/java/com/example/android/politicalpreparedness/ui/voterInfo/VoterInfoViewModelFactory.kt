package com.example.android.politicalpreparedness.ui.voterInfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.data.network.models.Division
import com.example.android.politicalpreparedness.data.repository.ElectionsRepository

//COMPLETED: Create Factory to generate VoterInfoViewModel with provided election datasource
class VoterInfoViewModelFactory(private val repository: ElectionsRepository, private val electionId: Int, private val division: Division) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VoterInfoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VoterInfoViewModel(repository, electionId, division) as T
        }
        throw IllegalArgumentException("Unable to construct ViewModel")
    }
}