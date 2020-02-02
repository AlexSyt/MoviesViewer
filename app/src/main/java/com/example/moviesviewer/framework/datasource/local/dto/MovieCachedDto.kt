package com.example.moviesviewer.framework.datasource.local.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieCachedDto(
    @PrimaryKey @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "poster_path") val posterPath: String?,
    @ColumnInfo(name = "bookmarked") val bookmarked: Boolean
)