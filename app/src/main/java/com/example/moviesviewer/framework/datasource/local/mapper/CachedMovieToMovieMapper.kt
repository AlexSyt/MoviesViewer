package com.example.moviesviewer.framework.datasource.local.mapper

import com.example.core.common.Mapper
import com.example.core.domain.model.Movie
import com.example.moviesviewer.framework.datasource.local.dto.MovieCachedDto

class CachedMovieToMovieMapper : Mapper<MovieCachedDto, Movie> {

    override fun map(item: MovieCachedDto): Movie =
        with(item) {
            Movie(id, title, description, posterPath, bookmarked)
        }
}