package com.beyond.utubeclone.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.beyond.utubeclone.R
import com.beyond.utubeclone.model.VidioModel
import com.bumptech.glide.Glide


class VidioRcAdapter: androidx.recyclerview.widget.ListAdapter<VidioModel, VidioRcAdapter.ViewHolder>(difUtil) {
    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        fun bind(item:VidioModel){
            val titleTV = itemView.findViewById<TextView>(R.id.titleTextView)
            val subTitleTV = itemView.findViewById<TextView>(R.id.subTitleTextView)
            val thumbnailImgV = itemView.findViewById<ImageView>(R.id.thumbnailImageView)

            titleTV.text = item.title
            subTitleTV.text = item.subtitle
            Glide.with(thumbnailImgV.context).load(item.thumb).override(400,230).centerCrop().into(thumbnailImgV)
            Log.d("텀브", "${item.thumb}")
        }

    }

    override fun getItemCount(): Int {
        Log.d("아이템 카운트", currentList.size.toString())
        return currentList.size
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_vidio_list_rc, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       return holder.bind(currentList[position])
    }
    companion object{
        val difUtil = object : DiffUtil.ItemCallback<VidioModel>(){
            override fun areItemsTheSame(oldItem: VidioModel, newItem: VidioModel): Boolean {
                return oldItem == newItem
                // 고유한 id값이 있어서, 그걸 체크하면 좋지만 일단 대충 만드는 거기도 하고, 아까 자료명에
                // id값 안넣어 줬으니 그냥 예전게 새거랑 어떻게 다른지 체크하는 기능만 달아줌
            }

            override fun areContentsTheSame(oldItem: VidioModel, newItem: VidioModel): Boolean {
                return oldItem == newItem
            }
        }
    }


}