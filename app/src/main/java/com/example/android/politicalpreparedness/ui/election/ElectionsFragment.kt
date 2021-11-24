package com.example.android.politicalpreparedness.ui.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ElectionsFragment : Fragment() {

    //COMPLETED: Declare ViewModel
    //Hilt DI - not using @Inject
    private val viewModel: ElectionsViewModel by viewModels()

    lateinit var binding: FragmentElectionBinding
    private lateinit var electionUpcomingAdapter: ElectionListAdapter
    private lateinit var electionFollowedAdapter: ElectionListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //COMPLETED: Add ViewModel values and create ViewModel
        //By Injection

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
        }).apply {
            setHasStableIds(true)
        }
        electionFollowedAdapter = ElectionListAdapter(ElectionListener { election ->
            viewModel.navigateToVoterInfo(election)
        }).apply {
            setHasStableIds(true)
        }

        //COMPLETED: Populate recycler adapters
        binding.recyclerviewUpcoming.adapter = electionUpcomingAdapter
        binding.recyclerviewSaved.adapter = electionFollowedAdapter

        val dividerItemDecoration =
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        binding.recyclerviewUpcoming.apply {
            addItemDecoration(dividerItemDecoration)
            setHasFixedSize(true)
        }
        binding.recyclerviewSaved.apply {
            addItemDecoration(dividerItemDecoration)
            setHasFixedSize(true)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //COMPLETED: Refresh adapters when fragment loads
        viewModel.upcomingElections.observe(
            viewLifecycleOwner,
            { electionUpcomingAdapter.submitList(it) })
        viewModel.savedElections.observe(
            viewLifecycleOwner,
            { electionFollowedAdapter.submitList(it) })
    }
}