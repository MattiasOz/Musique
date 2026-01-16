package com.matzuu.musique.models

import android.R
import android.annotation.SuppressLint
import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@SuppressLint("UnsafeOptInUsageError")
@Immutable
@Serializable
@Entity(tableName = "songs")
data class Song(
    @PrimaryKey
    @SerialName("id")
    val id: Long,

    @SerialName("historyEntryId")
    val historyEntryId: Long?,

    @SerialName("title")
    val title: String,

    @SerialName("path")
    val path: String,

    @SerialName("artist")
    val artist: String,

    @SerialName("album")
    val album: String,

    @SerialName("duration")
    val duration: Long,
)
