package com.example.android.politicalpreparedness.ui

import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.android.politicalpreparedness.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Utility functions for loading and formatting data in views.
 * Converted from BindingAdapters to support View Binding.
 */

fun ImageView.loadProfileImage(src: String?) {
    src?.let {
        val uri = it.toUri().buildUpon().scheme("https").build()
        Glide.with(this)
            .load(uri)
            .placeholder(R.drawable.ic_profile)
            .error(R.drawable.ic_profile)
            .fallback(R.drawable.ic_profile)
            .circleCrop()
            .into(this)
    } ?: run {
        this.setImageResource(R.drawable.ic_profile)
    }
}

fun Spinner.setStateValue(value: String?) {
    val adapter = toTypedAdapter<String>(this.adapter as? ArrayAdapter<*> ?: return)
    val position =
        when (adapter.getItem(0)) {
            is String -> adapter.getPosition(value)
            else -> this.selectedItemPosition
        }
    if (position >= 0) {
        setSelection(position)
    }
}

fun TextView.setFormattedDate(value: Date?) {
    val dateFormat = SimpleDateFormat("dd/MM/yyy", Locale.getDefault())
    text = value?.let {
        dateFormat.format(it)
    } ?: ""
}

fun ImageView.fetchImage(
    view: ImageView,
    src: String?,
) {
    view.loadProfileImage(src)
}

inline fun <reified T> toTypedAdapter(adapter: ArrayAdapter<*>): ArrayAdapter<T> = adapter as ArrayAdapter<T>
