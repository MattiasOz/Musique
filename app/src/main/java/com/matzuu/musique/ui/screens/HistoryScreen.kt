package com.matzuu.musique.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.matzuu.musique.models.Song
import com.matzuu.musique.ui.components.HistoryCard
import com.matzuu.musique.viewmodels.MusiqueViewModel


@Composable
fun HistoryScreen(
    musiqueViewModel: MusiqueViewModel,
    onClick : (List<Song>) -> Unit,
    modifier : Modifier = Modifier
) {
    LazyColumn(
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        items(10) {
            HistoryCard(
                historyEntry = historyEntry,
                onClick = onClick
            )
        }
    }
}

@Preview
@Composable
fun PreviewHistoryScreen() {
    HistoryScreen()
}

