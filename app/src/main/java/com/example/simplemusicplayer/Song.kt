package com.example.simplemusicplayer



import android.net.Uri
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit

data class Song(
    val id : String,
    val title : String,
    val album : String,
    val duration : Long = 0,
    val size : Long,
    val path : String
)
fun formatDuration(duration: Long):String{
    val minute = TimeUnit.MINUTES.convert(duration,TimeUnit.MILLISECONDS)
    val seconds = (TimeUnit.SECONDS.convert(duration,TimeUnit.MILLISECONDS) - minute * TimeUnit.SECONDS.convert(1,TimeUnit.MINUTES))
    return String.format("%2d:%02d",minute,seconds)
}
fun formatSize(size: Long):String{
    var tempSize = ""
    val kb = size/1024.0
    val mb = ((size/1024.0)/1024.0)
    val gb = (((size/1024.0)/1024.0)/1024.0)
    val tb = ((((size/1024.0)/1024.0)/1024.0)/1024.0)
    if(tb>1){
        tempSize =  DecimalFormat("0.00").format(tb)+"TB"
    }
    else if(gb>1){
        tempSize =  DecimalFormat("0.00").format(gb)+"GB"
    }
    else if(mb>1){
        tempSize =  DecimalFormat("0.00").format(mb)+"MB"
    }
    else if(kb>1){
        tempSize =  DecimalFormat("0.00").format(kb)+"KB"
    }
    return tempSize
}
