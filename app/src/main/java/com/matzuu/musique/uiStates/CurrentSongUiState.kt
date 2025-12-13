package com.matzuu.musique.uiStates

import com.matzuu.musique.models.Song

sealed interface CurrentSongUiState {
    data class Success(val song: Song) : CurrentSongUiState
    object Unset : CurrentSongUiState
    object Error : CurrentSongUiState
}
