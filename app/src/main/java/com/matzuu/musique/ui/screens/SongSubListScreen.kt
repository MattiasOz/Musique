package com.matzuu.musique.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.paging.compose.collectAsLazyPagingItems
import com.matzuu.musique.models.Song
import com.matzuu.musique.ui.components.HistoryNameDialog
import com.matzuu.musique.ui.components.ListScreen
import com.matzuu.musique.uiStates.CurrentPlaylistUiState
import com.matzuu.musique.uiStates.MusicListUiState
import com.matzuu.musique.viewmodels.MusiqueViewModel

private const val TAG = "SongSubListScreen"
@Composable
fun SongSubListScreen(
    musiqueViewModel: MusiqueViewModel,
    onSongClick: (List<Song>, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    val playlistName: String = when(val state = musiqueViewModel.currentPlaylistUiState) {
        is CurrentPlaylistUiState.Success -> {
            state.name
        }
        CurrentPlaylistUiState.Unset -> {
            "<Unset>"
        }
    }
    val songs = musiqueViewModel.pagedSubSongsFlow.collectAsLazyPagingItems()
    // TODO might want to replace "songs" with "allSongs" since it might be useless now
    when (val allSongs = musiqueViewModel.musicSubListUiState.collectAsState().value) {
        is MusicListUiState.Success -> {
            if (showDialog) {
                HistoryNameDialog(
                    text = playlistName,
                    onDismissRequest = {
                        showDialog = false
                    },
                    onConfirm = { text: String ->
                        Log.d(TAG, "History name: $text")
                        musiqueViewModel.createHistoryEntry(text, allSongs.songs)
                        musiqueViewModel.updateHistoryEntries()
                        showDialog = false
                    }
                )
            }
            Column() {
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = playlistName,
                        fontSize = TextUnit(20f, TextUnitType.Sp),
                        fontStyle = FontStyle.Italic,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(4f)
                    )
                    Button(
                        onClick = {
                            showDialog = true
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Add")
                    }
                }

                ListScreen(
                    songs = songs,
                    allSongs = allSongs.songs,
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
