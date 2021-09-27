package com.example.android.politicalpreparedness.election.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.databinding.ViewholderElectionBinding
import com.example.android.politicalpreparedness.network.models.Election

class ElectionListAdapter(private val clickListener: ElectionListener) :
    ListAdapter<Election, ElectionViewHolder>(ElectionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectionViewHolder {
        return ElectionViewHolder.from(parent)
    }

    //COMPLETED: Bind ViewHolder
    override fun onBindViewHolder(holder: ElectionViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(clickListener, item)
    }

}

//COMPLETED: Create ElectionViewHolder
class ElectionViewHolder(val binding: ViewholderElectionBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(clickListener: ElectionListener, item: Election) {
        binding.election = item
        binding.clickListener = clickListener

        binding.executePendingBindings()
    }


    //COMPLETED: Add companion object to inflate ViewHolder (from)
    companion object {
        //@LayoutRes
        //val LAYOUT = R.layout.viewholder_election

        fun from(parent: ViewGroup): ElectionViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ViewholderElectionBinding.inflate(layoutInflater, parent, false)

            return ElectionViewHolder(binding)
        }
    }
}

//COMPLETED: Create ElectionDiffCallback
class ElectionDiffCallback : DiffUtil.ItemCallback<Election>() {
    override fun areItemsTheSame(oldItem: Election, newItem: Election): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Election, newItem: Election): Boolean {
        return ((oldItem.id == newItem.id)
                && (oldItem.electionDay == newItem.electionDay)
                && (oldItem.name == newItem.name))
    }
}

//COMPLETED: Create ElectionListener
class ElectionListener(val clickListener: (election: Election) -> Unit) {
    fun onClick(election: Election) = clickListener(election)
}

