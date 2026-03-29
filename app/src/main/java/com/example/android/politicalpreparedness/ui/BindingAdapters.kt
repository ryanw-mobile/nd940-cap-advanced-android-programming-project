package com.example.android.politicalpreparedness.ui

import android.widget.ImageView
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.android.politicalpreparedness.R

fun fetchImage(
    view: ImageView,
    src: String?,
) {
    src?.let {
        val uri = it.toUri().buildUpon().scheme("https").build()
        Glide.with(view)
            .load(uri)
            .placeholder(R.drawable.ic_profile)
            .error(R.drawable.ic_profile)
            .fallback(R.drawable.ic_profile)
            .circleCrop()
            .into(view)
    } ?: run {
        view.setImageResource(R.drawable.ic_profile)
    }
}
