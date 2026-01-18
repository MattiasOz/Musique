package com.matzuu.musique.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName

@Entity(tableName = "historySongCrossRef", primaryKeys = ["historyEntryId", "songId"])
data class HistorySongCrossRef(
    @SerialName("historyEntryId")
    val historyEntryId: Long,

    @SerialName("songId")
    val songId: Long
)
