package com.example.moviesviewer.framework

import com.example.core.data.MoviesDataSource
import com.example.core.domain.interactor.GetMoviesUseCase
import com.example.core.domain.repository.MoviesRepository
import com.example.core.domain.repository.MoviesRepositoryImpl
import com.example.moviesviewer.BuildConfig
import com.example.moviesviewer.framework.datasource.remote.MoviesRemoteDataSource
import com.example.moviesviewer.framework.datasource.remote.api.MoviesApi
import com.example.moviesviewer.presentation.MoviesViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {

    // Network
    single { provideRetrofit() }
    single { provideMoviesApi(get()) }
    single { provideApiKey() }

    // DataSource
    single<MoviesDataSource> { MoviesRemoteDataSource(get(), get()) }

    // Repository
    single<MoviesRepository> { MoviesRepositoryImpl(get()) }

    // Interactor
    single { GetMoviesUseCase(get()) }

    // ViewModel
    viewModel { MoviesViewModel(get()) }
}

fun provideRetrofit(): Retrofit =
    Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/3/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

fun provideMoviesApi(retrofit: Retrofit): MoviesApi = retrofit.create(MoviesApi::class.java)

fun provideApiKey(): String = BuildConfig.API_KEY