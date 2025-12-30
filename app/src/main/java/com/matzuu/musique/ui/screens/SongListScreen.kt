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
    onSongClick: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    val songs = musiqueViewModel.pagedSongsFlow.collectAsLazyPagingItems()
    when (musiqueViewModel.musicListUiState.collectAsState().value) {
        is MusicListUiState.Success -> {
            ListScreen(
                songs = songs,
                onSongClick = onSongClick,
                modifier = modifier
            )
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

@Composable
private fun ListScreen(
    songs: LazyPagingItems<Song>,
    onSongClick: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        //state = LazyListState(5),
        modifier = modifier
    ) {
        items(
            count = songs.itemCount,
            key = songs.itemKey { it.id },
            contentType = { "song_card" }
        ) { idx ->
            val song = songs[idx]
            if (song != null) {
                SongCard(
                    song = song,
                    onClick = onSongClick
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