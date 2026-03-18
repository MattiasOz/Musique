package com.matzuu.musique.ui.screens

import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.matzuu.musique.models.Song
import com.matzuu.musique.ui.components.ListScreen
import com.matzuu.musique.uiStates.CurrentPlaylistUiState
import com.matzuu.musique.viewmodels.MusiqueViewModel

private const val TAG = "CurrentPlaylistScreen"
@Composable
fun CurrentPlaylistScreen(
    musiqueViewModel: MusiqueViewModel,
    onSongClick: (List<Song>, Int) -> Unit,
    scrollState: LazyListState = rememberLazyListState(),
    modifier: Modifier = Modifier
) {
    val playlist = musiqueViewModel.currentPlaylist
    val songIdx = musiqueViewModel.currentPlaylistIdx

    val songPath = try {
        val song = playlist[songIdx]
        song.path
    } catch (e: IndexOutOfBoundsException) {
        Log.e(TAG, "Index out of bounds, returning empty string", e)
        ""
    }
    ListScreen(
        songs = null,
        allSongs = playlist,
        onSongClick = onSongClick,
        scrollState = scrollState,
        selectedSongUrl = songPath,
        modifier = modifier
    )
}