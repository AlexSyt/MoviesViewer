package com.example.moviesviewer.framework.datasource.local.db

import androidx.room.*
import com.example.moviesviewer.framework.datasource.local.dto.MovieCachedDto

@Dao
interface MoviesDao {

    @Query("SELECT * FROM movies")
    suspend fun getMovies(): List<MovieCachedDto>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: MovieCachedDto)

    @Update
    suspend fun updateMovie(movie: MovieCachedDto)

    @Query("DELETE FROM movies")
    suspend fun deleteMovies()
}