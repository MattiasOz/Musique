package com.matzuu.musique.ui.components

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PauseCircleFilled
import androidx.compose.material.icons.outlined.PauseCircleOutline
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.matzuu.musique.uiStates.CurrentSongUiState
import com.matzuu.musique.viewmodels.MusiqueViewModel
import kotlinx.serialization.InternalSerializationApi

private const val TAG = "MusiqueBottomBar"

@Composable
fun MusiqueBottomBar(
    sliderValue: Float = 0.0f,
    onValueChange: (Float) -> Unit,
    onPlayPauseClick: () -> Unit,
    modifier: Modifier = Modifier
){
    val musiqueViewModel: MusiqueViewModel = viewModel(factory = MusiqueViewModel.Factory)
    val isPlaying = musiqueViewModel.isPlaying
    val enabled = musiqueViewModel.currentSongUiState is CurrentSongUiState.Success

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
        ){
            Slider(
                value = sliderValue,
                onValueChange = onValueChange,
                valueRange = 0f..1f,
                enabled = enabled
            )
            TextBar()
        }
        IconButton(
            onClick = {
                Log.d(TAG, "Play button clicked")
                onPlayPauseClick()
            },
            enabled = enabled,
            modifier = Modifier
                .defaultMinSize(minHeight = 70.dp)
                .fillMaxWidth()
        ) {
            Icon(
                imageVector = if(isPlaying) Icons.Outlined.Pause else Icons.Outlined.PlayArrow,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, InternalSerializationApi::class)
@Composable
private fun TextBar(){
    val musiqueViewModel: MusiqueViewModel = viewModel(factory = MusiqueViewModel.Factory)
    when(val state = musiqueViewModel.currentSongUiState) {
        is CurrentSongUiState.Success -> {
            //val songInfo = "${state.song.title} \n ${state.song.artist}"
            // TODO add current playlist viewer
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f) // don't know why this works
                        .padding(bottom = 8.dp)
                ) {
                    Text(
                        text = state.song.title,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                    )
                    Text(
                        text = state.song.artist,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                    )
                }
                CurrentTime(
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                )
            }
        }

        CurrentSongUiState.Unset -> {
            Text(
                text = "<No song selected>",
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                //color = Color(0xa0ffffff),
                modifier = Modifier
                    .alpha(0.5f)
            )
        }
        CurrentSongUiState.Error -> {
            Text(
                text = "Error",
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                color = Color.Red,
            )
        }
    }
}

@Composable
private fun CurrentTime(
    modifier: Modifier = Modifier
) {
    val musiqueViewModel: MusiqueViewModel = viewModel(factory = MusiqueViewModel.Factory)
    val current = musiqueViewModel.currentTime
    val currentHour = current / 3_600_000
    val currentMinutes = current / 60_000 % 60
    val currentSeconds = current / 1_000 % 60
    val duration = musiqueViewModel.totalTime
    val totalHour = duration / 3_600_000
    val totalMinutes = duration / 60_000 % 60
    val totalSeconds = duration / 1_000 % 60
    val currentTimeText = if (totalHour > 0) {
        "%d:%02d:%02d".format(currentHour, currentMinutes, currentSeconds)
    } else {
        "%d:%02d".format(currentMinutes, currentSeconds)
    }
    val totalTimeText = if (totalHour > 0) {
        "%d:%02d:%02d".format(totalHour, totalMinutes, totalSeconds)
    } else {
        "%d:%02d".format(totalMinutes, totalSeconds)
    }
    val fontSize = 16.sp
    Text(
        text = "$currentTimeText/$totalTimeText",
        fontSize = fontSize,
        textAlign = TextAlign.End,
        modifier = modifier
    )
    //Row(
    //    verticalAlignment = Alignment.CenterVertically,
    //    modifier = Modifier
    //        .padding(bottom = 8.dp)
    //){
    //    //Text(
    //    //    text = currentTimeText,
    //    //    fontSize = fontSize,
    //    //    textAlign = TextAlign.End,
    //    //    modifier = Modifier
    //    //        .weight(1f)
    //    //)
    //    //Text(
    //    //    text = "/",
    //    //    fontSize = fontSize,
    //    //    modifier = Modifier
    //    //        .weight(0.05f)
    //    //)
    //    //Text(
    //    //    text = totalTimeText,
    //    //    fontSize = fontSize,
    //    //    modifier = Modifier
    //    //        .weight(1f)
    //    //)
    //}
}

@Preview
@Composable
fun PreviewMusiqueBottomBar(){
    MusiqueBottomBar(
        sliderValue = 0.5f,
        onValueChange = {},
        onPlayPauseClick = {},
    )
}