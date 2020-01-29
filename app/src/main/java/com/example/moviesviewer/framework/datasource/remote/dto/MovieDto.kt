package com.example.moviesviewer.framework.datasource.remote.dto

import com.google.gson.annotations.SerializedName

data class MovieDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("overview") val description: String,
    @SerializedName("poster_path") val posterPath: String?
)