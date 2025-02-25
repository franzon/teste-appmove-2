package com.example.testeappmoove.data.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.testeappmoove.data.dao.LikeDao
import com.example.testeappmoove.data.entities.LikedMovie
import com.example.testeappmoove.data.entities.MovieDetails
import com.example.testeappmoove.data.entities.MovieResponse
import com.example.testeappmoove.data.network.MovieApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MovieRepository(private val likeDao: LikeDao) {

    // TODO: utilizar dependency injection
    val api = MovieApi()

    private val _popularMovies = MutableLiveData<MovieResponse>()
    val popularMovies: LiveData<MovieResponse>
        get() = _popularMovies

    // Carrega os filmes populares
    fun loadPopularMovies() {
        api.getPopularMovies().enqueue(object : Callback<MovieResponse> {
            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                val likes = likeDao.all()
                val likedIds = likes.map { it.id }

                val body = response.body()

                for (movie in body?.results!!) {
                    movie.liked = likedIds.contains(movie.id)
                }

                _popularMovies.value = response.body()
            }
        })
    }

    // Carrega mais filmes e concatena na lista
    fun loadMoreMovies(page: Int) {
        api.getPopularMovies(page).enqueue(object : Callback<MovieResponse> {
            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                val likes = likeDao.all()
                val likedIds = likes.map { it.id }

                val body = response.body()

                for (movie in body?.results!!) {
                    movie.liked = likedIds.contains(movie.id)
                }

                val results = response.body()?.results
                val current = popularMovies.value?.results

                Log.d("Loading", "Carregando mais filmes")

                current?.let {
                    results?.let {
                        val newResponse = MovieResponse(current.plus(results))
                        _popularMovies.value = newResponse
                    }
                }
            }
        })
    }

    // Retorna os detalhes de um filme pelo id
    fun getMovieDetails(movieId: Int): LiveData<MovieDetails> {
        val movieDetailsResponse = MutableLiveData<MovieDetails>()

        api.getMovieDetails(movieId).enqueue(object : Callback<MovieDetails> {
            override fun onFailure(call: Call<MovieDetails>, t: Throwable) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<MovieDetails>, response: Response<MovieDetails>) {

                val likes = likeDao.all()
                val likedIds = likes.map { it.id }

                val body = response.body()
                body?.liked = likedIds.contains(body?.id)

                movieDetailsResponse.value = response.body()
            }
        })

        return movieDetailsResponse
    }

    // Busca por filmes
    fun searchMovies(query: String): LiveData<MovieResponse> {
        val moviesResponse = MutableLiveData<MovieResponse>()

        api.searchMovies(query).enqueue(object : Callback<MovieResponse> {
            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                moviesResponse.value = response.body()
            }
        })

        return moviesResponse
    }

    // Favorita ou remove like de um filme
    fun likeMovie(movieId: Int) {
        val likes = likeDao.all()
        val likedIds = likes.map { it.id }

        val newPopularMovies = _popularMovies.value
        val movie = newPopularMovies?.results?.find { it.id == movieId }

        if (likedIds.contains(movieId)) {
            likeDao.delete(LikedMovie(movieId))
            movie?.liked = false
        } else {
            likeDao.insert(LikedMovie(movieId))
            movie?.liked = true
        }

        _popularMovies.value = newPopularMovies
    }

    // Singleton
    companion object {
        private var INSTANCE: MovieRepository? = null

        fun getInstance(likeDao: LikeDao): MovieRepository? {
            if (INSTANCE == null) {
                INSTANCE = MovieRepository(likeDao)
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}