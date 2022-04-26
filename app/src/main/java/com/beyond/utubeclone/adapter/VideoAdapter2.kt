package com.beyond.utubeclone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.beyond.utubeclone.R
import com.beyond.utubeclone.model.VidioModel
import com.bumptech.glide.Glide


class VideoAdapter2(val callback: (String, String) -> Unit) : ListAdapter<VidioModel, VideoAdapter2.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(item: VidioModel) {
            val titleTextView = view.findViewById<TextView>(R.id.titleTextView)
            val subTitleTextView = view.findViewById<TextView>(R.id.subTitleTextView)
            val thumbnailImageView = view.findViewById<ImageView>(R.id.thumbnailImageView)

            titleTextView.text = item.title
            subTitleTextView.text = item.subtitle

            Glide.with(thumbnailImageView.context)
                .load(item.thumb)
                .into(thumbnailImageView)

            view.setOnClickListener {
                callback(item.sources, item.title)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_vidio_list_rc, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<VidioModel>() {
            override fun areItemsTheSame(oldItem: VidioModel, newItem: VidioModel): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: VidioModel, newItem: VidioModel): Boolean {
                return oldItem == newItem
            }

        }
    }
}