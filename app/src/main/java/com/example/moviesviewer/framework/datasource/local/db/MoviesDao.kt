package com.example.moviesviewer.framework.datasource.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.moviesviewer.framework.datasource.local.dto.MovieCachedDto

@Dao
interface MoviesDao {

    @Query("SELECT * FROM movies")
    suspend fun getMovies(): List<MovieCachedDto>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: MovieCachedDto)

    @Query("DELETE FROM movies")
    suspend fun deleteMovies()
}