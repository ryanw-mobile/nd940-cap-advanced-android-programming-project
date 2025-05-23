package com.example.android.politicalpreparedness.domain.model

import com.example.android.politicalpreparedness.data.network.models.Office
import com.example.android.politicalpreparedness.data.network.models.Official

data class Representative(
    val official: Official,
    val office: Office,
)
