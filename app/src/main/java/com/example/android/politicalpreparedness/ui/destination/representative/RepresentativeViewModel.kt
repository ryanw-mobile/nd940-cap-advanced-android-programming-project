package com.example.android.politicalpreparedness.ui.destination.representative

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.data.network.CivicsApi
import com.example.android.politicalpreparedness.data.network.models.Address
import com.example.android.politicalpreparedness.di.DispatcherModule
import com.example.android.politicalpreparedness.domain.model.Representative
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class RepresentativeViewModel @Inject constructor(
    @DispatcherModule.MainDispatcher private val dispatcher: CoroutineDispatcher,
) : ViewModel() {
    private val _address = MutableLiveData<Address>()
    val address: LiveData<Address>
        get() = _address

    private val _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>>
        get() = _representatives

    init {
        _address.value = Address("", "", "", "Alabama", "")
    }

    /**
     *  The following code will prove helpful in constructing a representative from the API. This code combines the two nodes of the RepresentativeResponse into a single official :

    val (offices, officials) = getRepresentativesDeferred.await()
    _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }

    Note: getRepresentatives in the above code represents the method used to fetch data from the API
    Note: _representatives in the above code represents the established mutable live data housing representatives

     */
    fun fetchRepresentatives() {
        Timber.d("fetchRepresentatives - final address = {${_address.value!!.toFormattedString()}")
        viewModelScope.launch(dispatcher) {
            _address.value?.let {
                try {
                    val getRepresentativesDeferred =
                        CivicsApi.retrofitService.getRepresentatives(_address.value!!.toFormattedString())
                    // Important: No need to call await() from inside the Coroutine.
                    val (offices, officials) = getRepresentativesDeferred
                    _representatives.value =
                        offices.flatMap { office -> office.getRepresentatives(officials) }
                } catch (e: Exception) {
                    e.printStackTrace()
                    _representatives.value = emptyList()
                }
            }
        }
    }

    fun setAddress(newAddress: Address) {
        _address.value = newAddress
    }
}
