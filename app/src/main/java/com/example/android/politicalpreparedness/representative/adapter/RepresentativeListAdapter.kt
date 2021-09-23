package com.example.android.politicalpreparedness.representative.adapter

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
import com.example.android.politicalpreparedness.databinding.ViewholderRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Channel
import com.example.android.politicalpreparedness.representative.model.Representative

class RepresentativeListAdapter(val clickListener: RepresentativeListener) :
    ListAdapter<Representative, RepresentativeViewHolder>(RepresentativeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepresentativeViewHolder {
        return RepresentativeViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RepresentativeViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(clickListener, item)
    }
}

class RepresentativeViewHolder(val binding: ViewholderRepresentativeBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(clickListener: RepresentativeListener, item: Representative) {
        // XML retrieves string values for textviews
        binding.representative = item
        binding.clickListener = clickListener

        // Use Glide to take care of the profile image
        // BindingAdapter is defined to supply the URL from the XML
        // binding.representativePhoto.setImageResource(R.drawable.ic_profile)

        //COMPLETED: Show social links ** Hint: Use provided helper methods
        //COMPLETED: Show www link ** Hint: Use provided helper methods
        binding.twitterIcon.visibility = View.GONE
        binding.facebookIcon.visibility = View.GONE
        binding.wwwIcon.visibility = View.GONE

        if (item.official.channels != null) {
            showSocialLinks(item.official.channels)
        }
        if (item.official.urls != null) {
            showWWWLinks(item.official.urls)
        }

        binding.executePendingBindings()
    }

    //COMPLETED: Add companion object to inflate ViewHolder (from)
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

    private fun enableLink(view: ImageView, url: String) {
        view.visibility = View.VISIBLE
        view.setOnClickListener { setIntent(url) }
    }

    private fun setIntent(url: String) {
        val uri = Uri.parse(url)
        val intent = Intent(ACTION_VIEW, uri)
        itemView.context.startActivity(intent)
    }

}

//COMPLETED: Create RepresentativeDiffCallback
class RepresentativeDiffCallback : DiffUtil.ItemCallback<Representative>() {
    override fun areItemsTheSame(oldItem: Representative, newItem: Representative): Boolean {
        return oldItem.official.name == newItem.official.name
    }

    override fun areContentsTheSame(oldItem: Representative, newItem: Representative): Boolean {
        return oldItem == newItem
    }
}

//COMPLETED: Create RepresentativeListener
class RepresentativeListener(val clickListener: (representativeId: String) -> Unit) {
    fun onClick(representative: Representative) = clickListener(representative.official.name)
}
