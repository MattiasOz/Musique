package com.matzuu.musique.uiStates

import com.matzuu.musique.models.HistoryEntry

sealed interface HistoryListUiState {
    data class Success(val historyEntries: List<HistoryEntry>) : HistoryListUiState
    object Loading : HistoryListUiState
    object Error : HistoryListUiState
}