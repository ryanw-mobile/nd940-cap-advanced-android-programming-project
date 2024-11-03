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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
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

    lateinit var binding: FragmentRepresentativeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // COMPLETED: Establish bindings
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_representative, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

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

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        //COMPLETED: Handle location permission result to get location on permission granted
//    }

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

//    private fun checkLocationPermissions(): Boolean {
//        return if (isPermissionGranted()) {
//            true
//        } else {
//            //COMPLETED: Request Location permissions
//            false
//        }
//    }

//    private fun isPermissionGranted(): Boolean {
//        //COMPLETED Check if permission is already granted and return (true = granted, false = denied/other)
//        return true
//    }

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
