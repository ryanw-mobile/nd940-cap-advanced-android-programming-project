package com.example.android.politicalpreparedness.election

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
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener


class ElectionsFragment : Fragment() {

    //COMPLETED: Declare ViewModel
    lateinit var viewModel: ElectionsViewModel

    lateinit var binding: FragmentElectionBinding
    lateinit var electionUpcomingAdapter: ElectionListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //COMPLETED: Add ViewModel values and create ViewModel
        val electionDao = ElectionDatabase.getInstance(requireContext()).electionDao
        val viewModelFactory = ElectionsViewModelFactory(electionDao)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ElectionsViewModel::class.java)

        //COMPLETED: Add binding values
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_election, container, false)
        binding.lifecycleOwner = this
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

        //TODO: Add binding values
        electionUpcomingAdapter = ElectionListAdapter(ElectionListener { election ->
            findNavController().navigate(
                ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(
                    election.id, election.division
                )
            )
        })

        //TODO: Link elections to voter info
        binding.recyclerviewUpcoming.adapter = electionUpcomingAdapter

        //TODO: Initiate recycler adapters
        val dividerItemDecoration =
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        binding.recyclerviewUpcoming.addItemDecoration(dividerItemDecoration)

        //TODO: Populate recycler adapters
        return binding.root
    }

    //TODO: Refresh adapters when fragment loads
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.upcoomingElections.observe(
            viewLifecycleOwner,
            { electionUpcomingAdapter.submitList(it) })
}