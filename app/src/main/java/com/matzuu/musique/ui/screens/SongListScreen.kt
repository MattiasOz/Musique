package com.matzuu.musique.ui.screens

import android.util.Log
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.matzuu.musique.models.Song
import com.matzuu.musique.uiStates.MusicListUiState
import com.matzuu.musique.viewmodels.MusiqueViewModel

@Composable
fun SongListScreen(
    musiqueViewModel: MusiqueViewModel,
    modifier: Modifier = Modifier
) {
    when (val state = musiqueViewModel.musicListUiState) {
        is MusicListUiState.Success -> {
            ListScreen(
                songs = state.songs,
                modifier = modifier
            )
            if (state.songs.isEmpty()) {
                Text(text = "No songs found")
            }
        }
        MusicListUiState.Loading -> {
            Text(text = "Loading...")
        }
        MusicListUiState.Error -> {
            Text(text = "Error :(")
        }
    }
}

@Composable
private fun ListScreen(
    songs: List<Song>,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(songs) { song ->
            Text(text = song.title)
        }
    }
}