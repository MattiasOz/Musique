package com.matzuu.musique.uiStates

import com.matzuu.musique.models.HistoryEntry
import com.matzuu.musique.models.Song

sealed interface CurrentPlaylistUiState {
    data class Success(val name: String, val historyEntry: HistoryEntry?) : CurrentPlaylistUiState
    //data class Success(val name: String, val songs: List<Song>, val idx: Int) : CurrentPlaylistUiState
    object Unset : CurrentPlaylistUiState
}