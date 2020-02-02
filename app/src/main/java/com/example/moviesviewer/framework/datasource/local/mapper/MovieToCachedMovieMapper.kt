package com.example.moviesviewer.framework.datasource.local.mapper

import com.example.core.common.Mapper
import com.example.core.domain.model.Movie
import com.example.moviesviewer.framework.datasource.local.dto.MovieCachedDto

class MovieToCachedMovieMapper : Mapper<Movie, MovieCachedDto> {

    override fun map(item: Movie): MovieCachedDto =
        with(item) {
            MovieCachedDto(id, title, description, posterPath)
        }
}