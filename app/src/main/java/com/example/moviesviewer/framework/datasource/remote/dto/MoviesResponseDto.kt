package com.example.moviesviewer.framework.datasource.remote.dto

import com.google.gson.annotations.SerializedName

data class MoviesResponseDto(
    @SerializedName("page") val page: Int,
    @SerializedName("results") val movies: List<MovieDto>,
    @SerializedName("total_results") val totalResults: Int,
    @SerializedName("total_pages") val totalPages: Int
)