package com.matzuu.musique.database

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.matzuu.musique.models.HistoryEntry
import com.matzuu.musique.utils.SONG_SYNC_WORK_TAG
import com.matzuu.musique.workers.SongSyncWorker

interface SongRepository {

    fun enqueueWorker()
    suspend fun getHistoryEntries(): List<HistoryEntry>
}

class SongRepositoryImpl(
    ctx: Context,
    private val musicDao: MusicDao
) : SongRepository {
    private val workManager = WorkManager.getInstance(ctx)

    override fun enqueueWorker() {
        val constraints = Constraints.Builder()
            .build()

        val workRequest = OneTimeWorkRequestBuilder<SongSyncWorker>()
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniqueWork(SONG_SYNC_WORK_TAG, ExistingWorkPolicy.REPLACE, workRequest)
    }

    override suspend fun getHistoryEntries(): List<HistoryEntry> {
        return musicDao.getHistoryEntries()
    }
}