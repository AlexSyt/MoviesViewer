package com.example.moviesviewer.framework.datasource.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.moviesviewer.framework.datasource.local.dto.MovieCachedDto

@Database(entities = [MovieCachedDto::class], version = 1, exportSchema = false)
abstract class MoviesDatabase : RoomDatabase() {

    abstract fun moviesDao(): MoviesDao
}