package com.matzuu.musique.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.matzuu.musique.models.HistoryEntry
import com.matzuu.musique.models.Song
import kotlinx.serialization.InternalSerializationApi

@Dao
interface MusicDao {
    @OptIn(InternalSerializationApi::class)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongs(songs: List<Song>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFullSongList(songs: List<Song>)


    @OptIn(InternalSerializationApi::class)
    @Query("SELECT * FROM songs")
    suspend fun getAllSongs(): List<Song>

    @Query("DELETE FROM songs")
    suspend fun clearSongs()

    @OptIn(InternalSerializationApi::class)
    @Insert
    suspend fun insertHistoryEntry(historyEntry: HistoryEntry): Long

    @Insert
    suspend fun insertHistoryEntrySongs(songs: List<Song>)

    @OptIn(InternalSerializationApi::class)
    @Query("SELECT * FROM history")
    suspend fun getHistoryEntries(): List<HistoryEntry>

    @Query("SELECT * FROM songs WHERE historyEntryId = :historyEntryId")
    suspend fun getHistorySongs(historyEntryId: Long): List<Song>

    @Query("SELECT * FROM songs WHERE album = :album")
    suspend fun getSongsFromAlbum(album: String): List<Song>

    @Update
    suspend fun updateHistoryEntry(historyEntry: HistoryEntry)
}