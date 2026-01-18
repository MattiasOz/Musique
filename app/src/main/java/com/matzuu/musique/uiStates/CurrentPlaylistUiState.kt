package com.matzuu.musique.uiStates

sealed interface CurrentPlaylistUiState {
    data class Success(val name: String) : CurrentPlaylistUiState
    object Unset : CurrentPlaylistUiState
}