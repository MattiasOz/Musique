package com.matzuu.musique.models

import android.annotation.SuppressLint
import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
@Entity(tableName = "albums")
data class Album(
    @PrimaryKey
    @SerialName("id")
    val id: Long,

    @SerialName("title")
    val title: String,
)
