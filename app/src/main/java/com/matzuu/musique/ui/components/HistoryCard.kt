package com.matzuu.musique.ui.components

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.matzuu.musique.models.HistoryEntry
import com.matzuu.musique.models.Song

private const val TAG = "HistoryCard"

@Composable
fun HistoryCard(
    historyEntry: HistoryEntry,
    onClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = {
            Log.d(TAG, "Clicked on History Card")
            onClick(historyEntry.id)
        },
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            text = historyEntry.name,
            modifier = Modifier
                .padding(8.dp)
        )
    }
}

@Preview
@Composable
fun PreviewHistoryCard() {
    val historyEntry = HistoryEntry(
        name = "HistoryEntry",
        //songs = listOf(),
        songIdx = 0,
        timestamp = 0L
    )
    HistoryCard(historyEntry, {})
}
