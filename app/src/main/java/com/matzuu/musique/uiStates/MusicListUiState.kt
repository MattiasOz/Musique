package com.matzuu.musique.uiStates

import com.matzuu.musique.models.Song

sealed interface MusicListUiState {
    data class Success(val songs: List<Song>) : MusicListUiState
    object Loading : MusicListUiState
    object Error : MusicListUiState
}