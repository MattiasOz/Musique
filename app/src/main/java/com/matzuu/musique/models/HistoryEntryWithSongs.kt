package com.matzuu.musique.models

import androidx.room.Embedded
import androidx.room.Relation

data class HistoryEntryWithSongs(
    @Embedded
    val historyEntry: HistoryEntry,

    @Relation(
        parentColumn = "id",
        entityColumn = "historyEntryId"
    )
    val songs: List<Song>
)
