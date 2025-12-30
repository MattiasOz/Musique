package com.matzuu.musique.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.matzuu.musique.models.HistoryEntry
import com.matzuu.musique.models.Song
import kotlinx.serialization.InternalSerializationApi

@Dao
interface MusicDao {
    @OptIn(InternalSerializationApi::class)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongs(songs: List<Song>)

    @OptIn(InternalSerializationApi::class)
    @Query("SELECT * FROM songs")
    suspend fun getAllSongs(): List<Song>

    @Query("DELETE FROM songs")
    suspend fun clearSongs()

    @OptIn(InternalSerializationApi::class)
    @Insert
    suspend fun insertHistoryEntry(historyEntry: HistoryEntry)

    @OptIn(InternalSerializationApi::class)
    @Query("SELECT * FROM history")
    suspend fun getHistoryEntries(): List<HistoryEntry>
}