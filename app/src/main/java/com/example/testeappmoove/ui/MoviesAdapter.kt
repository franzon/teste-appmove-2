package com.example.testeappmoove.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.testeappmoove.R
import com.example.testeappmoove.data.Movie
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.first_movie_item.view.*
import kotlinx.android.synthetic.main.movie_item.view.*
import kotlinx.android.synthetic.main.movie_item.view.title

class MoviesAdapter(private val movies: List<Movie>) : RecyclerView.Adapter<MovieViewHolder>() {

    override fun getItemCount() = movies.size

    override fun getItemViewType(position: Int): Int {
        if (movies.isEmpty() || position == 0)
            return 0
        return 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        if (viewType == 0) {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.first_movie_item, parent, false)
            return MovieViewHolder(view)
        }
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.movie_item, parent, false)
        return MovieViewHolder(view)

    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        holder.title?.text = movie.title
        holder.overview?.text = movie.overview

        var imageView = holder?.imageView

        if (imageView != null) {
            Picasso.get().load("https://image.tmdb.org/t/p/w500/" + movie.backdrop_path)
                .into(imageView)
        }

    }

}

class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var title = itemView.title
    var imageView = itemView.imageView
    var overview = itemView.overview
}