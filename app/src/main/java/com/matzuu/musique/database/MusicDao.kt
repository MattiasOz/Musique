package com.matzuu.musique.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.matzuu.musique.models.Album
import com.matzuu.musique.models.HistoryEntry
import com.matzuu.musique.models.HistorySongCrossRef
import com.matzuu.musique.models.Song
import kotlinx.serialization.InternalSerializationApi

@Dao
interface MusicDao {
    @OptIn(InternalSerializationApi::class)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongs(songs: List<Song>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFullSongList(songs: List<Song>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFullAlbumList(albums: List<Album>)

    @OptIn(InternalSerializationApi::class)
    @Query("SELECT * FROM songs ORDER BY title")
    suspend fun getAllSongs(): List<Song>

    @Query("SELECT * FROM albums ORDER BY title")
    suspend fun getAllAlbums(): List<Album>

    @Query("DELETE FROM songs")
    suspend fun clearSongs()

    @OptIn(InternalSerializationApi::class)
    @Insert
    suspend fun insertHistoryEntry(historyEntry: HistoryEntry): Long

    @Insert
    suspend fun insertHistoryEntrySongs(songs: List<Song>)

    @Insert
    suspend fun insertHistorySongCrossRef(historySongCrossRefs: List<HistorySongCrossRef>)

    //@Query("SELECT * FROM historySongCrossRef WHERE historyEntryId = :historyEntryId")
    //suspend fun getHistorySongCrossRefs(historyEntryId: Long): List<HistorySongCrossRef>

    @Query("SELECT * FROM history WHERE id = :historyEntryId")
    suspend fun getHistoryEntry(historyEntryId: Long): HistoryEntry

    @OptIn(InternalSerializationApi::class)
    @Query("SELECT * FROM history")
    suspend fun getHistoryEntries(): List<HistoryEntry>

    @Transaction
    @Query("SELECT * FROM songs WHERE id IN (SELECT songId FROM historySongCrossRef WHERE historyEntryId = :historyEntryId)")
    suspend fun getHistorySongs(historyEntryId: Long): List<Song>

    @Query("SELECT * FROM songs WHERE album = :album")
    suspend fun getSongsFromAlbum(album: String): List<Song>

    @Update
    suspend fun updateHistoryEntry(historyEntry: HistoryEntry)
}