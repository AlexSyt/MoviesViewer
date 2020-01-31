package com.example.moviesviewer.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.core.domain.model.Movie
import com.example.moviesviewer.R
import kotlinx.android.synthetic.main.item_movie.view.*

class MoviesAdapter(
    private val shareAction: (Int) -> Unit
) : ListAdapter<Movie, MoviesAdapter.ViewHolder>(MovieDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder.from(parent).apply {
            shareBtn.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    shareAction(getItem(adapterPosition).id)
                }
            }
        }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val shareBtn: Button = itemView.shareBtn
        private val posterIv: ImageView = itemView.posterImageView
        private val titleTv: TextView = itemView.titleTextView
        private val descriptionIv: TextView = itemView.descriptionTextView

        fun bind(movie: Movie) {
            movie.posterPath?.let(posterIv::loadImage)
            titleTv.text = movie.title
            descriptionIv.text = movie.description
        }

        companion object {

            fun from(parent: ViewGroup): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val itemView = inflater.inflate(R.layout.item_movie, parent, false)
                return ViewHolder(itemView)
            }
        }
    }
}

class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {

    override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean = oldItem == newItem
}

private fun ImageView.loadImage(path: String) =
    Glide
        .with(context)
        .load(path)
        .centerCrop()
        .placeholder(R.drawable.ic_movie_placeholder)
        .into(this)