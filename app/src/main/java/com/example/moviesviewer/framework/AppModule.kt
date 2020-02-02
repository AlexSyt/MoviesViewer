package com.example.moviesviewer.framework

import android.content.Context
import androidx.room.Room
import com.example.core.data.MoviesDataSource
import com.example.core.domain.interactor.GetMoviesUseCase
import com.example.core.domain.repository.MoviesRepository
import com.example.core.domain.repository.MoviesRepositoryImpl
import com.example.moviesviewer.BuildConfig
import com.example.moviesviewer.framework.datasource.local.MoviesLocalDataSource
import com.example.moviesviewer.framework.datasource.local.db.MoviesDatabase
import com.example.moviesviewer.framework.datasource.remote.MoviesRemoteDataSource
import com.example.moviesviewer.framework.datasource.remote.api.MoviesApi
import com.example.moviesviewer.presentation.MoviesViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val remote: Qualifier = named("remote")
val local: Qualifier = named("local")

val appModule = module {

    // Network
    single { provideRetrofit() }
    single { provideMoviesApi(get()) }
    single { provideApiKey() }

    // Database
    single { provideMoviesDatabase(get()) }
    single { (get() as MoviesDatabase).moviesDao() }

    // DataSource
    single<MoviesDataSource>(remote) { MoviesRemoteDataSource(get(), get()) }
    single<MoviesDataSource>(local) { MoviesLocalDataSource(get()) }

    // Repository
    single<MoviesRepository> { MoviesRepositoryImpl(get(remote), get(local)) }

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

fun provideMoviesDatabase(context: Context): MoviesDatabase =
    Room.databaseBuilder(context.applicationContext, MoviesDatabase::class.java, "movies.db")
        .fallbackToDestructiveMigration()
        .build()