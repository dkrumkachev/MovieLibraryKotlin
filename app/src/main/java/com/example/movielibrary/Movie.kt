package com.example.movielibrary

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Movie(
    val id: String? = null,
    val title: String? = null,
    val year: Int? = null,
    val director: String? = null,
    val description: String? = null,
    val images: List<String>? = null
) : Parcelable
