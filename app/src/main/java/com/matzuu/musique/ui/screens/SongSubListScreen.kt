package com.matzuu.musique.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.paging.compose.collectAsLazyPagingItems
import com.matzuu.musique.models.Song
import com.matzuu.musique.ui.components.ListScreen
import com.matzuu.musique.uiStates.MusicListUiState
import com.matzuu.musique.viewmodels.MusiqueViewModel

@Composable
fun SongSubListScreen(
    musiqueViewModel: MusiqueViewModel,
    onSongClick: (Song) -> Unit,
    playlistName: String,
    modifier: Modifier = Modifier
) {
    val songs = musiqueViewModel.pagedSubSongsFlow.collectAsLazyPagingItems()
    when (musiqueViewModel.musicSubListUiState.collectAsState().value) {
        is MusicListUiState.Success -> {
            Column() {
                Text(text = playlistName)
                ListScreen(
                    songs = songs,
                    onSongClick = onSongClick,
                    modifier = modifier
                )
            }
            if (songs.itemCount <= 0) {
                Text(text = "No songs found")
            }
        }
        MusicListUiState.Loading -> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "Loading...",
                    textAlign = TextAlign.Center,
                )
            }
        }
        MusicListUiState.Error -> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "Error :(",
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
