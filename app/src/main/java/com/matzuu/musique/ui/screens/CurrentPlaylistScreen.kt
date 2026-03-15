package com.matzuu.musique.ui.screens

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.matzuu.musique.models.Song
import com.matzuu.musique.ui.components.ListScreen
import com.matzuu.musique.viewmodels.MusiqueViewModel

@Composable
fun CurrentPlaylistScreen(
    musiqueViewModel: MusiqueViewModel,
    onSongClick: (Int) -> Unit,
    scrollState: LazyListState = rememberLazyListState(),
    modifier: Modifier = Modifier
) {
    val playlist = musiqueViewModel.currentPlaylist
    val songIdx = musiqueViewModel.currentPlaylistIdx

    // TODO add highlight to current song
    ListScreen(
        songs = null,
        allSongs = playlist,
        onSongClick = { _: List<Song>, idx: Int ->
            onSongClick(idx)
        },
        scrollState = scrollState,
        modifier = modifier
    )
}