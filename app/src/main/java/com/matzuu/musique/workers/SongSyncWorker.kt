package com.matzuu.musique.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.matzuu.musique.utils.fileSearch
import com.matzuu.musique.viewmodels.MusiqueViewModel

private const val TAG = "SongSyncWorker"

class SongSyncWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val songs = fileSearch(context)
        try {
            viewmodel!!.setSongs(songs = songs)
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