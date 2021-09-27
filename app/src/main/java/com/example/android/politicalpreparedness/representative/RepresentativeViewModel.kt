package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class RepresentativeViewModel : ViewModel() {

    //COMPLETED: Establish live data for representatives and address
    private val _address = MutableLiveData<Address>()
    val address: LiveData<Address>
        get() = _address

    private val _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>>
        get() = _representatives

    init {
        _address.value = Address("", "", "", "", "")
    }

    //COMPLETED: Create function to fetch representatives from API from a provided address
    /**
     *  The following code will prove helpful in constructing a representative from the API. This code combines the two nodes of the RepresentativeResponse into a single official :

    val (offices, officials) = getRepresentativesDeferred.await()
    _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }

    Note: getRepresentatives in the above code represents the method used to fetch data from the API
    Note: _representatives in the above code represents the established mutable live data housing representatives

     */
    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)
    fun fetchRepresentatives() {
        coroutineScope.launch {
            _address.value?.let {
                val getPropertiesDeferred =
                    CivicsApi.retrofitService.getRepresentatives(address.value!!.toFormattedString())
                try {
                    val (offices, officials) = getPropertiesDeferred.await()
                    _representatives.value =
                        offices.flatMap { office -> office.getRepresentatives(officials) }
                } catch (e: Exception) {
                    e.printStackTrace()
                    _representatives.value = emptyList()
                }
            }
        }
    }

    override fun onCleared() {
        viewModelJob.cancel()
        super.onCleared()
    }

    //COMPLETED: Create function get address from geo location
    fun setAddress(newAddress: Address) {
        _address.value = newAddress
    }

    //COMPLETED: Create function to get address from individual fields
    fun setAddress(line1: String, line2: String, city: String, state: String, zip: String) {
        setAddress(Address(line1, line2, city, state, zip))
    }

}
