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
import com.matzuu.musique.models.Song

private const val TAG = "FileSearch"

fun findFiles(
    context: Context
) {
    // TODO: integrate the database into this
}

fun fileSearch(
    context: Context
) : List<Song> {


    val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.DISPLAY_NAME,
        MediaStore.Audio.Media.DURATION,
    )

    //val selection = "${MediaStore.Audio.Media.MIME_TYPE} IN (${FILE_EXTENSIONS.map{"?, "}})" //TODO this? might not work
    val selection = "${MediaStore.Audio.Media.MIME_TYPE} IN (?, ?)"
    val selectionArgs = FILE_EXTENSIONS

    val sortOrder = "${MediaStore.Audio.Media.DISPLAY_NAME} ASC"

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

        Log.d(TAG, "File search started ${it.moveToFirst()}")
        while (it.moveToNext()) {
            val id =it.getLong(idColumn)
            val name = it.getString(nameColumn)

            // Do something with the file ID and name
            val song = Song(id, name, "", "", 10) // TODO: get artist, album, duration
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