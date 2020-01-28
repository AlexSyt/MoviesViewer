package com.example.core.domain

data class Movie(
    val id: Int,
    val title: String,
    val description: String,
    val posterPath: String?
)