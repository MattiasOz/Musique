package com.matzuu.musique.workers

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.matzuu.musique.models.Album
import com.matzuu.musique.utils.fileSearch
import com.matzuu.musique.viewmodels.MusiqueViewModel
import kotlinx.serialization.InternalSerializationApi

private const val TAG = "SongSyncWorker"

class SongSyncWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    @OptIn(InternalSerializationApi::class)
    override suspend fun doWork(): Result {
        val songs = fileSearch(context)
        try {
            viewmodel!!.apply {
                setSongs(songs = songs)

                val albums = songs.distinctBy{
                    it.album
                }.map { song ->
                    Album(
                        id = song.id,
                        title = song.album,
                    )
                }.sortedBy {
                    it.title
                }
                setAlbums(albums = albums)
            }
        } catch (e: NullPointerException) {
            Log.e(TAG, "viewmodel is null")
            return Result.failure()
        }
        Log.d(TAG, "SongSyncWorker finished")
        return Result.success()
    }


    companion object {
        var viewmodel: MusiqueViewModel? = null
    }
}