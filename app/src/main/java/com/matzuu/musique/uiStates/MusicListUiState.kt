package com.matzuu.musique.uiStates

import com.matzuu.musique.models.Song
import kotlinx.serialization.InternalSerializationApi

sealed interface MusicListUiState {
    data class Success @OptIn(InternalSerializationApi::class) constructor(val songs: List<Song>) : MusicListUiState
    object Loading : MusicListUiState
    object Error : MusicListUiState
}