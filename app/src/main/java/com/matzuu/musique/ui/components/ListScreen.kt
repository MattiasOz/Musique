package com.matzuu.musique.ui.components

import android.util.Log
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.matzuu.musique.models.Song

private const val TAG = "ListScreen"

@Composable
fun ListScreen(
    songs: LazyPagingItems<Song>, //TODO remove this? If things work okay anyway
    allSongs: List<Song>,
    onSongClick: (List<Song>, Int) -> Unit,
    scrollState: LazyListState = rememberLazyListState(),
    modifier: Modifier = Modifier
) {
    LazyColumn(
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        state = scrollState,
        modifier = modifier
            .fillMaxSize()
    ) {
        items(
            //count = songs.itemCount,
            //key = songs.itemKey { it.id },
            count = allSongs.size,
            contentType = { "song_card" }
        ) { idx ->
            //val song = songs[idx]
            val song = allSongs[idx]
            val onClick = {
                onSongClick(allSongs, idx)
            }
            if (song != null) {
                SongCard(
                    song = song,
                    onClick = onClick
                )
            }
        }
    }
}

