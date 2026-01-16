package com.matzuu.musique.models

import android.annotation.SuppressLint
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
@Entity(tableName = "history")
data class HistoryEntry (
    @PrimaryKey(autoGenerate = true)
    @SerialName("id")
    val id: Long = 0L,

    @SerialName("name")
    val name: String,

    //@SerialName("songs")
    //val songs : List<Song>,

    @SerialName("songIdx")
    val songIdx: Int,

    @SerialName("timestamp")
    val timestamp: Long,
)
