package com.matzuu.musique.utils

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.matzuu.musique.models.Album
import com.matzuu.musique.models.Song
import kotlinx.serialization.InternalSerializationApi

private const val TAG = "FileSearch"

fun findFiles(
    context: Context
) {
    // TODO: integrate the database into this
}

@OptIn(InternalSerializationApi::class)
fun fileSearch(
    context: Context
) : List<Song> {
    val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.DISPLAY_NAME,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.DATA,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ALBUM,
    )

    //val selection = "${MediaStore.Audio.Media.MIME_TYPE} IN (${FILE_EXTENSIONS.map{"?, "}})" //TODO this? might not work
    val selection = "${MediaStore.Audio.Media.MIME_TYPE} IN (?, ?)"
    val selectionArgs = FILE_EXTENSIONS

    val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

    val cursor = context.contentResolver.query(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        projection,
        selection,
        selectionArgs,
        sortOrder
    )

    val res: MutableList<Song> = mutableListOf()
    cursor?.use {
        val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
        val nameColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
        val durationColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
        val pathColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
        val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
        val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
        val albumColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)

        Log.d(TAG, "File search started ${it.moveToFirst()}")
        while (it.moveToNext()) {
            val id = it.getLong(idColumn)
            //val name = it.getString(nameColumn)
            val title = it.getString(titleColumn)
            val path = it.getString(pathColumn)
            val duration = it.getLong(durationColumn)
            val artist = it.getString(artistColumn)
            val album = it.getString(albumColumn)

            // Do something with the file ID and name
            val song = Song(id, title, path, artist, album, duration)
            res.add(song)
        }
    }
    Log.d(TAG, "File search complete")

    return res
    // this works!
//    val mediaPlayer = MediaPlayer()
//    val contentUri = ContentUris.withAppendedId(
//        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//        1000081522
//    )
//
//    mediaPlayer.setDataSource(context, contentUri)
//    mediaPlayer.prepare() // Prepare asynchronously
//    mediaPlayer.start()
}