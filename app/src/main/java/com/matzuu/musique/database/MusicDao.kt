package com.matzuu.musique.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.matzuu.musique.models.Song

@Dao
interface MusicDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongs(songs: List<Song>)

    @Query("SELECT * FROM songs")
    suspend fun getAllSongs(): List<Song>

    @Query("DELETE FROM songs")
    suspend fun clearSongs()
}