package com.matzuu.musique.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
@Entity(tableName = "songs")
data class Song(
    @PrimaryKey
    @SerialName("id")
    val id: Long,

    @SerialName("title")
    val title: String,

    @SerialName("artist")
    val artist: String,

    @SerialName("album")
    val album: String,

    @SerialName("duration")
    val duration: Long,
)
