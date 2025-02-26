package com.example.android.politicalpreparedness.ui.destination.representative

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.data.network.models.Channel
import com.example.android.politicalpreparedness.databinding.ViewholderRepresentativeBinding
import com.example.android.politicalpreparedness.domain.model.Representative

class RepresentativeListAdapter :
    ListAdapter<Representative, RepresentativeViewHolder>(RepresentativeDiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ) = RepresentativeViewHolder.from(parent)

    override fun onBindViewHolder(
        holder: RepresentativeViewHolder,
        position: Int,
    ) = holder.bind(getItem(position))

    // This is needed for hasStableIds()
    override fun getItemId(position: Int): Long {
        return getItem(position).hashCode().toLong()
    }
}

class RepresentativeViewHolder(private val binding: ViewholderRepresentativeBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Representative) {
        // XML retrieves string values for text views
        binding.representative = item

        // TextView values are handled at XML

        // Use Glide to take care of the profile image
        // BindingAdapter is defined to supply the URL from the XML
        // binding.representativePhoto.setImageResource(R.drawable.ic_profile)

        // COMPLETED: Show social links ** Hint: Use provided helper methods
        // COMPLETED: Show www link ** Hint: Use provided helper methods
        View.GONE.let {
            binding.twitterIcon.visibility = it
            binding.facebookIcon.visibility = it
            binding.wwwIcon.visibility = it
        }

        item.official.channels?.let {
            showSocialLinks(it)
        }
        item.official.urls?.let {
            showWWWLinks(it)
        }

        binding.executePendingBindings()
    }

    // COMPLETED: Add companion object to inflate ViewHolder (from)
    companion object {
        fun from(parent: ViewGroup): RepresentativeViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ViewholderRepresentativeBinding.inflate(layoutInflater, parent, false)

            return RepresentativeViewHolder(binding)
        }
    }

    private fun showSocialLinks(channels: List<Channel>) {
        val facebookUrl = getFacebookUrl(channels)
        if (!facebookUrl.isNullOrBlank()) {
            enableLink(binding.facebookIcon, facebookUrl)
        }

        val twitterUrl = getTwitterUrl(channels)
        if (!twitterUrl.isNullOrBlank()) {
            enableLink(binding.twitterIcon, twitterUrl)
        }
    }

    private fun showWWWLinks(urls: List<String>) {
        enableLink(binding.wwwIcon, urls.first())
    }

    private fun getFacebookUrl(channels: List<Channel>): String? {
        return channels.filter { channel -> channel.type == "Facebook" }
            .map { channel -> "https://www.facebook.com/${channel.id}" }
            .firstOrNull()
    }

    private fun getTwitterUrl(channels: List<Channel>): String? {
        return channels.filter { channel -> channel.type == "Twitter" }
            .map { channel -> "https://www.twitter.com/${channel.id}" }
            .firstOrNull()
    }

    private fun enableLink(
        view: ImageView,
        url: String,
    ) {
        view.visibility = View.VISIBLE
        view.setOnClickListener { setIntent(url) }
    }

    private fun setIntent(url: String) {
        val uri = Uri.parse(url)
        val intent = Intent(ACTION_VIEW, uri)
        itemView.context.startActivity(intent)
    }
}

// COMPLETED: Create RepresentativeDiffCallback
class RepresentativeDiffCallback : DiffUtil.ItemCallback<Representative>() {
    override fun areItemsTheSame(
        oldItem: Representative,
        newItem: Representative,
    ) = oldItem === newItem

    override fun areContentsTheSame(
        oldItem: Representative,
        newItem: Representative,
    ) = oldItem == newItem
}

// IGNORED: Create RepresentativeListener
// Note: in this implementation, the viewholder itself is not clickable but the buttons inside
// Therefore this Listener is redundant
