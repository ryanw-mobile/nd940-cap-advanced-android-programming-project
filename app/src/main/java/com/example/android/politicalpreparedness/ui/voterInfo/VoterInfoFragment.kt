package com.example.android.politicalpreparedness.ui.voterInfo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.data.repository.ElectionsRepository
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import dagger.hilt.android.AndroidEntryPoint
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
//        val args = VoterInfoFragmentArgs.fromBundle(requireArguments())

        // COMPLETED: Add ViewModel values and create ViewModel
//        val viewModelFactory =
        //          VoterInfoViewModelFactory(electionsRepository, args.argElectionId, args.argDivision)
//        val viewModel =
//            ViewModelProvider(this, viewModelFactory).get(VoterInfoViewModel::class.java)

        // COMPLETED: Add binding values
        val binding: FragmentVoterInfoBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_voter_info, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        // COMPLETED: Populate voter info -- hide views without provided data.
        /**
         Hint: You will need to ensure proper data is provided from previous fragment.
         */
        viewModel.voterInfoLoadError.observe(viewLifecycleOwner, { error ->
            if (error) {
                binding.dataScreen.visibility = View.GONE
                binding.loadingScreen.visibility = View.GONE
                binding.errorScreen.visibility = View.VISIBLE
            }
        })

        // Control the visibility of the State Correspondence
        // Could have a better way to do this
        viewModel.voterInfo.observe(viewLifecycleOwner, {
            // When it changes and have valid values, we update the UI
            binding.apply {
                addressGroup.visibility =
                    if (viewModel!!.getCorrespondenceAddress().isNullOrEmpty()) {
                        View.GONE
                    } else {
                        View.VISIBLE
                    }

                // COMPLETED: Handle loading of URLs
                stateLocations.setOnClickListener { openWebUrl(viewModel!!.getVotingLocationUrl()) }
                stateBallot.setOnClickListener { openWebUrl(viewModel!!.getBallotInformationUrl()) }
            }
        })

        // COMPLETED: Handle save button UI state
        // XML interacting with ViewModel directly

        // COMPLETED: cont'd Handle save button clicks
        // XML calling ViewModel function directly

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
