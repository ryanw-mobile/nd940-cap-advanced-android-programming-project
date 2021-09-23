package com.example.android.politicalpreparedness

import java.text.SimpleDateFormat

fun java.util.Date.toSimpleString(): String {
    val format = SimpleDateFormat("dd/MM/yyy")
    return format.format(this)
}