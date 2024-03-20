package com.example.simplemusicplayer


import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.simplemusicplayer.databinding.EachSongCardBinding


class SongAdaptor(val context : Context,val songsList : ArrayList<Song>): RecyclerView.Adapter<SongAdaptor.ViewHolder>() {
    class ViewHolder(val binding : EachSongCardBinding) :RecyclerView.ViewHolder(binding.root){
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(EachSongCardBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.songTitle.text =songsList.get(position).title
        holder.binding.songTitle.isSelected = true
        holder.binding.duration.text = formatDuration(songsList.get(position).duration)
        holder.binding.size.text = formatSize(songsList.get(position).size)
        Glide.with(context).load(songsList.get(position).album).apply(RequestOptions().placeholder(R.drawable.music).centerCrop()).into(holder.binding.songImage)
        holder.itemView.setOnClickListener {
            val intent = Intent(context,PlayerActivity::class.java)
            intent.putExtra("index",position)
            intent.putExtra("class","SongAdaptor")
            ContextCompat.startActivity(context,intent,null)
        }
    }

    override fun getItemCount(): Int {
        return songsList.size
    }
}


