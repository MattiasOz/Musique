package com.matzuu.musique.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.matzuu.musique.models.Song
import com.matzuu.musique.ui.components.HistoryCard
import com.matzuu.musique.uiStates.HistoryListUiState
import com.matzuu.musique.viewmodels.MusiqueViewModel


@Composable
fun HistoryScreen(
    musiqueViewModel: MusiqueViewModel,
    onClick : (Long) -> Unit,
    modifier : Modifier = Modifier
) {
    when(val state = musiqueViewModel.historyListUiState) {
        is HistoryListUiState.Success -> {
            LazyColumn(
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = modifier.fillMaxWidth()
            ) {
                items(state.historyEntries) { historyEntry ->
                    HistoryCard(
                        historyEntry = historyEntry,
                        onClick = onClick
                    )
                }
            }
        }
        HistoryListUiState.Loading -> {
            Text(text = "Loading...")
        }
        HistoryListUiState.Error -> {
            Text(text = "Error :( ")
        }
    }
}
