package com.matzuu.musique.uiStates

import com.matzuu.musique.models.Album

interface AlbumListUiState {
    data class Success(val albums: List<Album>) : AlbumListUiState
    object Loading : AlbumListUiState
    object Error : AlbumListUiState
}