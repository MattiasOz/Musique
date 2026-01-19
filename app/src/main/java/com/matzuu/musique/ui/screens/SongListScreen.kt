package com.matzuu.musique.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.matzuu.musique.models.Song
import com.matzuu.musique.ui.components.ListScreen
import com.matzuu.musique.ui.components.SongCard
import com.matzuu.musique.uiStates.MusicListUiState
import com.matzuu.musique.viewmodels.MusiqueViewModel
import kotlinx.serialization.InternalSerializationApi
import okhttp3.internal.filterList

private const val TAG = "SongListScreen"

@OptIn(InternalSerializationApi::class)
@Composable
fun SongListScreen(
    musiqueViewModel: MusiqueViewModel,
    onSongClick: (List<Song>, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val songs = musiqueViewModel.pagedSongsFlow.collectAsLazyPagingItems()
    when (val state = musiqueViewModel.musicListUiState.collectAsState().value) {
        is MusicListUiState.Success -> {
            if (songs.itemCount <= 0) {
                Text(text = "No songs found")
            } else {
                ListScreen(
                    songs = songs,
                    allSongs = state.songs,
                    onSongClick = onSongClick,
                    scrollState = musiqueViewModel.homeListScrollState,
                    modifier = modifier
                )
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

/*
@Preview(showBackground = true)
@Composable
private fun ListScreenPreview() {
    val songs = listOf<Song>(
        Song(1, "Song1", "Artist1", "Album1", 2),
        Song(2, "Song2", "Artist2", "Album1", 2),
        Song(2, "Den bästa och absoluta bnästastea låten som någon sin har skrivits av ett marsvin och ingen kan säga något annat då de aldrig har hört den", "Artist2", "Album1", 2)
    )
    ListScreen(
        songs = songs,
        {}
    )
}
*/