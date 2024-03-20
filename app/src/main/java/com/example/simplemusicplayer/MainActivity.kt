package com.example.simplemusicplayer

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplemusicplayer.databinding.ActivityMainBinding
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val STORAGE_PERMISSION_CODE = 101

    companion object {
        var musicList: ArrayList<Song> = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkPermission(android.Manifest.permission.READ_MEDIA_AUDIO, STORAGE_PERMISSION_CODE)
        binding.searching.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                filterList(p0)
                return true
            }

        })

    }


    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                permission
            ) == PackageManager.PERMISSION_DENIED
        ) {
            // Requesting the permission
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission), requestCode)
        } else {
            Toast.makeText(this@MainActivity, "Permission already granted", Toast.LENGTH_SHORT)
                .show()
            fetchSongs();
        }
    }

    @SuppressLint("Recycle", "Range")
    private fun fetchSongs() {
        val songList = ArrayList<Song>()
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
        var sortOrder = MediaStore.Audio.Media.DATE_ADDED + " DESC"
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.SIZE
        )
        val cursor = this.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            sortOrder,
            null
        )
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val idC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                    val titleC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val albumC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                            .toString()
                    val uri = Uri.parse("content://media/external/audio/albumart")
                    val artUric = Uri.withAppendedPath(uri, albumC).toString()
                    val sizeC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE))
                    val durationC =
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val song = Song(
                        id = idC,
                        title = titleC,
                        album = artUric,
                        duration = durationC,
                        size = sizeC,
                        path = pathC
                    )
                    val file = File(song.path)
                    if (file.exists())
                        songList.add(song)
                } while (cursor.moveToNext())
                cursor.close()
            }
        }
        musicList = songList

        adaptorFunction(musicList)

    }

    private fun adaptorFunction(mList: ArrayList<Song>) {
        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        var adaptor = SongAdaptor(this, mList)
        binding.recyclerView.adapter = adaptor
    }


    private fun filterList(text: String?) {
        var filterList = ArrayList<Song>()
        if (text != null) {
            for (i in musicList) {
                if (i.title.lowercase(Locale.ROOT).contains(text)) {
                    filterList.add(i)
                }
            }
            Log.d("MMMM", filterList.toString())
        }
        if (filterList.isEmpty()) {
            Toast.makeText(this, "No Data Found", Toast.LENGTH_SHORT).show()
        } else {
            adaptorFunction(filterList)
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@MainActivity, "Storage Permission Granted", Toast.LENGTH_SHORT)
                    .show()
                fetchSongs();
            } else {
                Toast.makeText(this@MainActivity, "Storage Permission Denied", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }


}