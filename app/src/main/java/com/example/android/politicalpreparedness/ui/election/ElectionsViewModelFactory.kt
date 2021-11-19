package com.example.android.politicalpreparedness.ui.election

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.data.network.repository.ElectionsRepository

//COMPLETED: Create Factory to generate ElectionViewModel with provided election datasource
class ElectionsViewModelFactory(private val repository: ElectionsRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ElectionsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ElectionsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unable to construct ViewModel")
    }
}