package com.example.android.politicalpreparedness.ui.destination.representative

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.android.politicalpreparedness.BuildConfig
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.data.network.models.Address
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.Locale

@AndroidEntryPoint
class RepresentativeFragment : Fragment() {
    // COMPLETED: Declare ViewModel
    // Hilt DI - not using @Inject
    private val viewModel: RepresentativeViewModel by viewModels()

    private lateinit var binding: FragmentRepresentativeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // COMPLETED: Establish bindings
        binding = FragmentRepresentativeBinding.inflate(inflater, container, false)

        // COMPLETED: Define and assign Representative adapter
        val representativesAdapter =
            RepresentativeListAdapter().apply {
                setHasStableIds(true)
            }
        binding.representativeRecyclerview.adapter = representativesAdapter

        val dividerItemDecoration =
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        binding.representativeRecyclerview.addItemDecoration(dividerItemDecoration)

        // COMPLETED: Populate Representative adapter
        viewModel.representatives.observe(
            viewLifecycleOwner,
            { representativesAdapter.submitList(it) },
        )

        // Sync address fields from ViewModel to UI (e.g. when location is used)
        viewModel.address.observe(viewLifecycleOwner) { address -> updateAddressFields(address) }

        // Sync EditText changes back to ViewModel address
        setupAddressTextWatchers()

        // COMPLETED: Establish button listeners for field and location search
        binding.state.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    viewModel.address.value?.state = binding.state.selectedItem as String
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long,
                ) {
                    viewModel.address.value?.state = binding.state.selectedItem as String
                }
            }
        binding.buttonLocation.setOnClickListener { checkAndRequestLocationPermissionsAndGetLocation() }
        binding.buttonSearch.setOnClickListener {
            hideKeyboard()
            viewModel.fetchRepresentatives()
        }

        return binding.root
    }

    private fun updateAddressFields(address: com.example.android.politicalpreparedness.data.network.models.Address) {
        if (binding.addressLine1.text.toString() != address.line1) binding.addressLine1.setText(address.line1)
        if (binding.addressLine2.text.toString() != (address.line2 ?: "")) binding.addressLine2.setText(address.line2 ?: "")
        if (binding.city.text.toString() != address.city) binding.city.setText(address.city)
        if (binding.zip.text.toString() != address.zip) binding.zip.setText(address.zip)
        @Suppress("UNCHECKED_CAST")
        val spinnerAdapter = binding.state.adapter as? ArrayAdapter<String>
        val position = spinnerAdapter?.getPosition(address.state) ?: -1
        if (position >= 0 && binding.state.selectedItemPosition != position) binding.state.setSelection(position)
    }

    private fun setupAddressTextWatchers() {
        binding.addressLine1.doAfterTextChanged { text ->
            viewModel.address.value?.let { if (it.line1 != text.toString()) it.line1 = text.toString() }
        }
        binding.addressLine2.doAfterTextChanged { text ->
            viewModel.address.value?.let { if (it.line2 != text.toString()) it.line2 = text.toString() }
        }
        binding.city.doAfterTextChanged { text ->
            viewModel.address.value?.let { if (it.city != text.toString()) it.city = text.toString() }
        }
        binding.zip.doAfterTextChanged { text ->
            viewModel.address.value?.let { if (it.zip != text.toString()) it.zip = text.toString() }
        }
    }

    /**
     * The following permission request has been rewritten using the latest offering from Google.
     * This was suggested by the mentor when doing the Project 4.
     */
    private fun checkAndRequestLocationPermissionsAndGetLocation() {
        when (
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
        ) {
            PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                Timber.d("checkAndRequestLocationPermissionsAndGetLocation: permission granted")
                getLocation()
            }

            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                Timber.d("checkAndRequestLocationPermissionsAndGetLocation: permission not granted")
                requestLocationPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                )
            }
        }
    }

    // Register the permissions callback, which handles the user's response to the
    // system permissions dialog. Save the return value, an instance of
    // ActivityResultLauncher. You can use either a val, as shown in this snippet,
    // or a lateinit var in your onAttach() or onCreate() method.
    private val requestLocationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
                Timber.d("requestLocationPermissionLauncher: permission granted")
                getLocation()
            } else {
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
                Snackbar.make(
                    binding.root,
                    getString(R.string.error_no_location_permission),
                    Snackbar.LENGTH_LONG,
                ).setAction(getString(R.string.settings)) {
                    // If user chose deny and don't ask again, at least this is a way to change it
                    startActivity(
                        Intent().apply {
                            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        },
                    )
                }.show()
            }
        }

    /**
     * Permission check has been skipped because this function is called only after checking for permission
     */
    @SuppressLint("MissingPermission")
    private fun getLocation() {
        // COMPLETED: Get location from LocationServices
        // COMPLETED: The geoCodeLocation method is a helper function to change the lat/long location to a human readable street address
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity()) {
            if (it != null) {
                geoCodeLocation(it)?.let { address -> viewModel.setAddress(address) }
                    .also { address ->
                        Timber.d("getLocation(): {$address.toFormattedString()}")
                    }
            } else {
                Timber.d("getLocation(): null result")
                Snackbar.make(
                    binding.root,
                    getString(R.string.error_null_location),
                    Snackbar.LENGTH_LONG,
                ).show()
            }
        }
    }

    // TODO: worked around to return null for getwell
    private fun geoCodeLocation(location: Location): Address? {
        context?.let { context ->
            val geocoder = Geocoder(context, Locale.getDefault())
            return geocoder.getFromLocation(location.latitude, location.longitude, 1)
                ?.map { address ->
                    Address(
                        // Geocoder might return null for all fields
                        address?.thoroughfare ?: "",
                        address?.subThoroughfare ?: "",
                        address?.locality ?: "",
                        address?.adminArea ?: "",
                        address?.postalCode ?: "",
                    )
                }
                ?.first()
        } ?: return null
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }
}
