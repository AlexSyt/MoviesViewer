package com.example.moviesviewer.framework.datasource.remote.mapper

import com.example.core.common.Mapper
import com.example.core.domain.model.Movie
import com.example.moviesviewer.framework.datasource.remote.dto.MovieDto

class MovieDtoToMovieMapper : Mapper<MovieDto, Movie> {

    override fun map(item: MovieDto): Movie =
        with(item) {
            Movie(id, title, description, "$BASE_URL/$posterPath")
        }

    companion object {
        private const val BASE_URL = "https://image.tmdb.org/t/p/w185"
    }
}