package com.matzuu.musique.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.matzuu.musique.models.Song

@Composable
fun ListScreen(
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

