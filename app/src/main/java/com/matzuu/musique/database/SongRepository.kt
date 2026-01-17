package com.matzuu.musique.database

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.matzuu.musique.models.Album
import com.matzuu.musique.models.HistoryEntry
import com.matzuu.musique.models.Song
import com.matzuu.musique.utils.SONG_SYNC_WORK_TAG
import com.matzuu.musique.workers.SongSyncWorker

interface SongRepository {

    fun enqueueWorker()
    suspend fun insertFullSongList(songs: List<Song>)
    suspend fun insertFullAlbumList(albums: List<Album>)
    suspend fun insertHistoryEntry(historyEntry: HistoryEntry, songs: List<Song>)
    suspend fun getAlbumList() : List<Album>
    suspend fun getFullSongList(): List<Song>
    suspend fun getHistoryEntries(): List<HistoryEntry>
    suspend fun getHistorySongs(historyEntryId: Long): List<Song>
    suspend fun getSongsFromAlbum(album: String): List<Song>
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

    override suspend fun insertFullSongList(songs: List<Song>) {
        musicDao.insertFullSongList(songs)
    }

    override suspend fun insertFullAlbumList(albums: List<Album>) {
        musicDao.insertFullAlbumList(albums)
    }

    override suspend fun insertHistoryEntry(
        historyEntry: HistoryEntry,
        songs: List<Song>
    ) {
        val id = musicDao.insertHistoryEntry(historyEntry)
        val songs2 = songs.map { song ->
            song.copy(historyEntryId = id)
        }
        musicDao.insertHistoryEntrySongs(songs2)
    }

    override suspend fun getAlbumList(): List<Album> {
        return musicDao.getAllAlbums()
    }

    override suspend fun getFullSongList(): List<Song> {
        return musicDao.getAllSongs()
    }

    override suspend fun getHistoryEntries(): List<HistoryEntry> {
        return musicDao.getHistoryEntries()
    }

    override suspend fun getHistorySongs(historyEntryId: Long): List<Song> {
        return musicDao.getHistorySongs(historyEntryId)
    }

    override suspend fun getSongsFromAlbum(album: String): List<Song> {
        return musicDao.getSongsFromAlbum(album)
    }
}