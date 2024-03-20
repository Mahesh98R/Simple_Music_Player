package com.example.simplemusicplayer

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.simplemusicplayer.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private lateinit var runnable : Runnable
    companion object {
        var musicListPA : ArrayList<Song> = ArrayList()
        var songPosition = 0
        var isPlaying : Boolean = false
        var mediaPlayer : MediaPlayer? = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        songPosition = intent.getIntExtra("index",0)
        when(intent.getStringExtra("class")){
            "SongAdaptor" -> {
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.musicList)
                layoutDesign()
                mediaPlayer()
            }
        }
        Log.d("SSSSS", musicListPA.size.toString())
        binding.playPauseBtn.setOnClickListener {
            if(isPlaying){
                pauseMusic()
            }
            else{
                playMusic()
            }
        }
        binding.previousSongBtn.setOnClickListener {
            songChanging(false)
        }
        binding.nextSongBtn.setOnClickListener {
            songChanging(true)
        }
        binding.seekBar.progress = 0
        binding.seekBar.max = mediaPlayer!!.duration
        binding.endDuration.text = formatDuration(musicListPA.get(songPosition).duration)
        binding.backIcon.setOnClickListener {
            onBackPressed()
        }
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progres: Int, fromUser: Boolean) {
                if (fromUser){
                    mediaPlayer!!.seekTo(progres)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) { }

            override fun onStopTrackingTouch(p0: SeekBar?) { }
        })
        val handler = Handler()
        runnable=Runnable{
            binding.seekBar.progress = mediaPlayer!!.currentPosition
            binding.startDuration.text = createTime(mediaPlayer!!.currentPosition)
            handler.postDelayed(runnable,1000)
        }
        handler.postDelayed(runnable,1000)

    }

    private fun createTime(time: Int): String {
        var timeLable = ""
        val min = time / 1000 / 60
        val sec = time / 1000 % 60
        timeLable = "$min:"
        if(sec<10){
            timeLable += "0"
        }
        timeLable += sec
        return timeLable

    }


    private fun layoutDesign(){
        Glide.with(this).load(musicListPA.get(songPosition).album).apply(RequestOptions().placeholder(R.drawable.music).centerCrop()).into(binding.songImage)
        binding.songName.text = musicListPA.get(songPosition).title
    }
    private fun mediaPlayer(){
        try{
            if(mediaPlayer == null) mediaPlayer = MediaPlayer()
            mediaPlayer!!.reset()
            mediaPlayer!!.setDataSource(musicListPA.get(songPosition).path)
            mediaPlayer!!.prepare()
            mediaPlayer!!.start()
            isPlaying = true
            binding.playPauseBtn.setIconResource(R.drawable.ic_pause)
        }
        catch (e : Exception){
            return
        }
    }
    private fun playMusic(){
        binding.playPauseBtn.setIconResource(R.drawable.ic_pause)
        isPlaying = true
        mediaPlayer!!.start()
    }
    private fun pauseMusic(){
        binding.playPauseBtn.setIconResource(R.drawable.ic_play)
        isPlaying = false
        mediaPlayer!!.pause()
    }
    private fun songChanging(increment : Boolean){
        if(increment){
            if(songPosition != musicListPA.size - 1) {
                songPosition += 1
                layoutDesign()
                mediaPlayer()
                binding.endDuration.text = formatDuration(musicListPA.get(songPosition).duration)
            }
            else{
                Toast.makeText(this,"You are in Last Song of the List",Toast.LENGTH_LONG).show()
            }
        }
        else{
            if(songPosition > 0){
                songPosition -= 1
                layoutDesign()
                mediaPlayer()
                binding.endDuration.text = formatDuration(musicListPA.get(songPosition).duration)
            }
            else{
                Toast.makeText(this,"You are in first Song of the List",Toast.LENGTH_LONG).show()
            }
        }
    }


}