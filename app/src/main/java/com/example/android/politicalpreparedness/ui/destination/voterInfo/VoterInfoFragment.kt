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
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class VoterInfoFragment : Fragment() {
    @Inject
    lateinit var electionsRepository: ElectionsRepository

    // Hilt DI - not using @Inject
    private val viewModel: VoterInfoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // COMPLETED: Add binding values
        val binding = FragmentVoterInfoBinding.inflate(inflater, container, false)

        // COMPLETED: Handle save button clicks
        binding.buttonSave.setOnClickListener { viewModel.toggleFollowElection() }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.loadingScreen.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // COMPLETED: Handle save button UI state
        viewModel.isFollowed.observe(viewLifecycleOwner) { isFollowed ->
            binding.buttonSave.text = getString(
                if (isFollowed == true) R.string.unfollow_election else R.string.follow_election,
            )
        }

        // Hint: You will need to ensure proper data is provided from previous fragment.
        viewModel.voterInfoLoadError.observe(viewLifecycleOwner) { error ->
            if (error) {
                binding.dataScreen.visibility = View.GONE
                binding.loadingScreen.visibility = View.GONE
                binding.errorScreen.visibility = View.VISIBLE
            }
        }

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        viewModel.voterInfo.observe(viewLifecycleOwner) { voterInfo ->
            binding.electionName.text = voterInfo.election.name
            binding.electionDate.text = dateFormat.format(voterInfo.election.electionDay)
            binding.address.text = viewModel.getCorrespondenceAddress() ?: ""

            // Control the visibility of the State Correspondence
            binding.addressGroup.visibility =
                if (viewModel.getCorrespondenceAddress().isNullOrEmpty()) View.GONE else View.VISIBLE

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
