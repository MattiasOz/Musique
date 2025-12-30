package com.matzuu.musique

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.matzuu.musique.models.Song
import com.matzuu.musique.ui.components.MusiqueBottomBar
import com.matzuu.musique.ui.components.TabTopBar
import com.matzuu.musique.ui.screens.AlbumGridScreen
import com.matzuu.musique.ui.screens.HistoryScreen
import com.matzuu.musique.ui.screens.SongListScreen
import com.matzuu.musique.viewmodels.MusiqueViewModel
import com.matzuu.musique.workers.SongSyncWorker
import kotlinx.serialization.InternalSerializationApi

private const val TAG = "MainScreen"

@OptIn(InternalSerializationApi::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
) {
    val musiqueViewModel: MusiqueViewModel = viewModel(factory = MusiqueViewModel.Factory)
    SongSyncWorker.viewmodel = musiqueViewModel
    musiqueViewModel.scheduleWorker()
    musiqueViewModel.updateValues()

    val player = musiqueViewModel.mediaPlayer

    val onSongClick = { song: Song ->
        Log.d(TAG, "Song clicked: $song")
        player.stop()
        player.reset()
        player.setDataSource(song.path)
        player.prepare() //TODO Async?
        player.start()
        musiqueViewModel.isPlaying = true
        musiqueViewModel.setCurrentSong(song)
    }
    val changeSongSliderPos = { position: Float ->
        Log.d(TAG, "Slider position changed to $position")
        Log.d(TAG, "Current position: ${player.currentPosition/1000}")
        Log.d(TAG, "Duration: ${player.duration/1000}")
        Log.d(TAG, "Is playing: ${player.timestamp}")
        player.seekTo((position * player.duration).toInt())
        musiqueViewModel.songSliderPosition = position
    }
    val onPlayPauseClick = {
        if(player.isPlaying) {
            player.pause()
            musiqueViewModel.isPlaying = false
        } else {
            player.start()
            musiqueViewModel.isPlaying = true
        }
    }

    val navController = rememberNavController()

    val onHistoryEntryClick = { songs: List<Song> ->
        Log.d(TAG, "History entry clicked")
        musiqueViewModel.setSongs(songs)
        navController.navigate(Screen.Home.name)
    }

    Scaffold(
        topBar = {
            TabTopBar(
                buttonNames = listOf("Songs", "Albums", "History"),
                onClicks = listOf(
                    {
                        Log.d(TAG, "Songs clicked")
                        navController.navigate(Screen.Home.name)
                    },
                    {
                        Log.d(TAG, "Albums clicked")
                        navController.navigate(Screen.Albums.name)
                    },
                    {
                        Log.d(TAG, "History clicked")
                    }
                )
            )
            //Text(
            //    text = "Musique",
            //    fontFamily = FontFamily.Serif,
            //    fontSize = 50.sp,
            //    textAlign = TextAlign.Center,
            //    modifier = Modifier
            //        .fillMaxWidth()
            //        .padding(top = 8.dp)
            //)
        },
        bottomBar = {
            MusiqueBottomBar(
                sliderValue = musiqueViewModel.songSliderPosition,
                onValueChange = changeSongSliderPos,
                onPlayPauseClick = onPlayPauseClick
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.name,
            modifier = modifier
                .padding(innerPadding)
        ) {
            composable(route = Screen.Home.name) {
                SongListScreen(
                    musiqueViewModel,
                    onSongClick = onSongClick
                )
            }
            composable(route = Screen.Albums.name) {
                AlbumGridScreen(musiqueViewModel)
            }
            composable(route = Screen.History.name) {
                HistoryScreen(
                    musiqueViewModel,
                    onClick = onHistoryEntryClick
                )
            }
        }
    }
}