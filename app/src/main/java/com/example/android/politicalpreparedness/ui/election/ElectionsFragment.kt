package com.example.android.politicalpreparedness.ui.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.data.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.ui.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.ui.election.adapter.ElectionListener
import com.example.android.politicalpreparedness.data.network.repository.ElectionsRepository


class ElectionsFragment : Fragment() {

    //COMPLETED: Declare ViewModel
    lateinit var viewModel: ElectionsViewModel

    lateinit var binding: FragmentElectionBinding
    private lateinit var electionUpcomingAdapter: ElectionListAdapter
    private lateinit var electionFollowedAdapter: ElectionListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //COMPLETED: Add ViewModel values and create ViewModel
        //This project does not grade for testing (so have not implemented)
        // - but the database, repository, and viewModel are all separated for ease of unit tests
        val electionsRepository =
            ElectionsRepository(ElectionDatabase.getInstance(requireContext()))
        val viewModelFactory = ElectionsViewModelFactory(electionsRepository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ElectionsViewModel::class.java)

        //COMPLETED: Add binding values
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_election, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        //COMPLETED: Link elections to voter info
        viewModel.selectedElection.observe(viewLifecycleOwner, { election ->
            election?.let {
                findNavController().navigate(
                    ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(
                        election.id, election.division
                    )
                )
                viewModel.doneNavigateToVoterInfo()
            }
        })

        //COMPLETED: Initiate recycler adapters
        electionUpcomingAdapter = ElectionListAdapter(ElectionListener { election ->
            viewModel.navigateToVoterInfo(election)
        })
        electionFollowedAdapter = ElectionListAdapter(ElectionListener { election ->
            viewModel.navigateToVoterInfo(election)
        })

        //COMPLETED: Populate recycler adapters
        binding.recyclerviewUpcoming.adapter = electionUpcomingAdapter
        binding.recyclerviewSaved.adapter = electionFollowedAdapter

        val dividerItemDecoration =
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        binding.recyclerviewUpcoming.addItemDecoration(dividerItemDecoration)
        binding.recyclerviewSaved.addItemDecoration(dividerItemDecoration)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //COMPLETED: Refresh adapters when fragment loads
        viewModel.upcoomingElections.observe(
            viewLifecycleOwner,
            { electionUpcomingAdapter.submitList(it) })
        viewModel.savedElections.observe(
            viewLifecycleOwner,
            { electionFollowedAdapter.submitList(it) })
    }
}