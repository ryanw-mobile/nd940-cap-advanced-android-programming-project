package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.network.repository.ElectionsRepository


class VoterInfoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //COMPLETED: Populate voter info -- hide views without provided data.
        /**
        Hint: You will need to ensure proper data is provided from previous fragment.
         */
        val args = VoterInfoFragmentArgs.fromBundle(requireArguments())

        //COMPLETED: Add ViewModel values and create ViewModel
        val electionsRepository =
            ElectionsRepository(ElectionDatabase.getInstance(requireContext()))
        val viewModelFactory =
            VoterInfoViewModelFactory(electionsRepository, args.argElectionId, args.argDivision)
        val viewModel =
            ViewModelProvider(this, viewModelFactory).get(VoterInfoViewModel::class.java)

        //COMPLETED: Add binding values
        val binding: FragmentVoterInfoBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_voter_info, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel


        //TODO: Handle loading of URLs
        //COMPLETED: Handle save button UI state
        //XML interacting with ViewModel directly

        // COMPLETED: cont'd Handle save button clicks
        // XML calling ViewModel function directly

        return binding.root
    }

    //COMPLETED: Create method to load URL intents
    private fun openWebUrl(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

}