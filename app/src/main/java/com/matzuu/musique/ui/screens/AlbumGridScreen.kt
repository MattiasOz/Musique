package com.matzuu.musique.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridCells.*
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.matzuu.musique.ui.components.AlbumCard
import com.matzuu.musique.uiStates.AlbumListUiState
import com.matzuu.musique.uiStates.MusicListUiState
import com.matzuu.musique.viewmodels.MusiqueViewModel

private const val TAG = "AlbumGridScreen"

@Composable
fun AlbumGridScreen(
    musiqueViewModel: MusiqueViewModel
) {
    when(val state = musiqueViewModel.albumListUiState) {
        is AlbumListUiState.Success -> {
            LazyVerticalGrid(
                columns = Fixed(3)
            ) {
                items(items = state.albums) { album ->
                    AlbumCard(
                        album = album,
                        onClick = {}
                    )
                }
            }
        }
        AlbumListUiState.Loading -> {
            Text(text = "Loading albums...")
        }
        AlbumListUiState.Error -> {
            Text(text = "Error :(")
        }
    }
}