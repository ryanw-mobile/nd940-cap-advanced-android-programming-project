package com.example.android.politicalpreparedness.ui.destination.voterInfo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.data.repository.ElectionsRepository
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.ui.setFormattedDate
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class VoterInfoFragment : Fragment() {
    @Inject
    lateinit var electionsRepository: ElectionsRepository

    // Hilt DI - not using @Inject
    private val viewModel: VoterInfoViewModel by viewModels()

    private lateinit var binding: FragmentVoterInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
//        val args = VoterInfoFragmentArgs.fromBundle(requireArguments())

        // Use View Binding instead of Data Binding
        binding = FragmentVoterInfoBinding.inflate(inflater, container, false)

        // Observe loading state and update UI
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.loadingScreen.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observe follow status and update button
        viewModel.isFollowed.observe(viewLifecycleOwner) { isFollowed ->
            binding.buttonSave.text = if (isFollowed) {
                getString(R.string.unfollow_election)
            } else {
                getString(R.string.follow_election)
            }
        }

        // Set button click listener
        binding.buttonSave.setOnClickListener {
            viewModel.toggleFollowElection()
        }

        // Hint: You will need to ensure proper data is provided from previous fragment.
        viewModel.voterInfoLoadError.observe(
            viewLifecycleOwner,
        ) { error ->
            if (error) {
                binding.dataScreen.visibility = View.GONE
                binding.loadingScreen.visibility = View.GONE
                binding.errorScreen.visibility = View.VISIBLE
            }
        }

        // Observe voter info and update UI
        viewModel.voterInfo.observe(viewLifecycleOwner) { voterInfo ->
            // Update election name and date
            binding.electionName.text = voterInfo?.election?.name ?: ""
            binding.electionDate.setFormattedDate(voterInfo?.election?.electionDay)

            // Control the visibility of the State Correspondence
            val correspondenceAddress = viewModel.getCorrespondenceAddress()
            if (correspondenceAddress.isNullOrEmpty()) {
                binding.addressGroup.visibility = View.GONE
            } else {
                binding.address.text = correspondenceAddress
                binding.addressGroup.visibility = View.VISIBLE
                }

            // COMPLETED: Handle loading of URLs
            binding.stateLocations.setOnClickListener { openWebUrl(viewModel.getVotingLocationUrl()) }
            binding.stateBallot.setOnClickListener { openWebUrl(viewModel.getBallotInformationUrl()) }
        }

        return binding.root
    }

    // COMPLETED: Create method to load URL intents
    private fun openWebUrl(url: String?) {
        url?.let {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
            startActivity(browserIntent)
        }
    }
}
