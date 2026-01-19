package com.matzuu.musique

import android.util.Log
import androidx.activity.result.launch
import androidx.compose.animation.EnterTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.matzuu.musique.models.Song
import com.matzuu.musique.ui.components.MusiqueBottomBar
import com.matzuu.musique.ui.components.TabTopBar
import com.matzuu.musique.ui.screens.AlbumGridScreen
import com.matzuu.musique.ui.screens.HistoryScreen
import com.matzuu.musique.ui.screens.SongListScreen
import com.matzuu.musique.ui.screens.SongSubListScreen
import com.matzuu.musique.viewmodels.MusiqueViewModel
import com.matzuu.musique.workers.SongSyncWorker
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi

private const val TAG = "MainScreen"

@OptIn(InternalSerializationApi::class, ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
) {
    val musiqueViewModel: MusiqueViewModel = viewModel(factory = MusiqueViewModel.Factory)
    SongSyncWorker.viewmodel = musiqueViewModel
    //musiqueViewModel.scheduleWorker()
    musiqueViewModel.updateValues()

    val player = musiqueViewModel.mediaPlayer

    player.setOnCompletionListener { player ->
        val songs = musiqueViewModel.currentPlaylist
        val idx = musiqueViewModel.currentPlaylistIdx
        player.pause()
        if (idx >= songs.size - 1) {
            musiqueViewModel.isPlaying = false
            return@setOnCompletionListener
        }
        val nextSong = songs[idx+1]
        player.reset()
        player.setDataSource(nextSong.path)
        player.prepare() //TODO Async?
        player.start()
        musiqueViewModel.isPlaying = true
        musiqueViewModel.setCurrentSong(nextSong)
        musiqueViewModel.currentPlaylistIdx += 1
    }

    val onSongClick = { songs: List<Song>, idx: Int ->
        Log.d(TAG, "Song clicked ${songs[idx]}")
        musiqueViewModel.currentPlaylist = songs
        musiqueViewModel.currentPlaylistIdx = idx
        val song = songs[idx]
        player.stop()
        player.reset()
        player.setDataSource(song.path)
        player.prepare() //TODO Async?
        player.start()
        musiqueViewModel.isPlaying = true
        musiqueViewModel.setCurrentSong(song)
    }
    /*val onSongClick = { song: Song ->
        Log.d(TAG, "Song clicked: $song")
        player.stop()
        player.reset()
        player.setDataSource(song.path)
        player.prepare() //TODO Async?
        player.start()
        musiqueViewModel.isPlaying = true
        musiqueViewModel.setCurrentSong(song)
    }*/
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

    val onHistoryEntryClick = { historyEntryId: Long ->
        Log.d(TAG, "History entry clicked")
        musiqueViewModel.setSubSongsFromHistoryEntryId(historyEntryId)
        navController.navigate(Screen.SubList.name)
    }

    val onAlbumClick = { albumName: String ->
        Log.d(TAG, "Album clicked")
        musiqueViewModel.setSubSongsFromAlbum(albumName)
        navController.navigate(Screen.SubList.name)
    }

    val topBarRoute = when(navController.currentBackStackEntryAsState().value?.destination?.route) {
        Screen.Home.name -> 0
        Screen.Albums.name -> 1
        Screen.History.name -> 2
        else -> -1
    }

    // --- Pager Setup ---
    val pagerScreens = listOf(Screen.Home, Screen.Albums, Screen.History)
    val pagerState = rememberPagerState(pageCount = { pagerScreens.size })
    val coroutineScope = rememberCoroutineScope()

    // This effect syncs the Pager's state to the NavController
    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress) {
            navController.navigate(pagerScreens[pagerState.currentPage].name) {
                // Avoid multiple copies of the same destination when re-selecting the same item
                launchSingleTop = true
                // Restore state when re-selecting a previously selected item
                restoreState = true
            }
        }
    }

    // This effect syncs the NavController's state to the Pager
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    LaunchedEffect(navBackStackEntry) {
        val currentPageIndex = pagerScreens.indexOfFirst { it.name == navBackStackEntry?.destination?.route }
        if (currentPageIndex != -1 && currentPageIndex != pagerState.currentPage) {
            coroutineScope.launch {
                pagerState.animateScrollToPage(currentPageIndex)
            }
        }
    }
    // --- End Pager Setup ---
    val pager = @Composable {
        HorizontalPager(state = pagerState) { page ->
            when (page) {
                0 -> SongListScreen(
                    musiqueViewModel,
                    onSongClick = onSongClick
                )

                1 -> AlbumGridScreen(
                    musiqueViewModel,
                    onClick = onAlbumClick
                )

                2 -> HistoryScreen(
                    musiqueViewModel,
                    onClick = onHistoryEntryClick
                )
            }
        }
    }

    Scaffold(
        topBar = {
            TabTopBar(
                buttonNames = listOf("Songs", "Albums", "History"),
                currentRoute = topBarRoute,
                imageVectorsFilled = listOf(
                    Icons.Filled.MusicNote,
                    Icons.Filled.LibraryMusic,
                    Icons.Filled.History
                ),
                imageVectorsOutlined = listOf(
                    Icons.Outlined.MusicNote,
                    Icons.Outlined.LibraryMusic,
                    Icons.Outlined.History
                ),
                onClicks = listOf(
                    {
                        navController.navigate(
                            route = Screen.Home.name,
                            navOptions = NavOptions.Builder()
                                .setPopUpTo(
                                    route = Screen.Home.name,
                                    inclusive = true
                                ).build()
                        )
                    },
                    {
                        navController.navigate(Screen.Albums.name,
                            navOptions = NavOptions.Builder()
                            .setPopUpTo(
                                route = Screen.Albums.name,
                                inclusive = true
                            ).build()
                        )
                    },
                    {
                        navController.navigate(Screen.History.name,
                            navOptions = NavOptions.Builder()
                            .setPopUpTo(
                                route = Screen.History.name,
                                inclusive = true
                            ).build()
                        )
                    }
                ),
                updateOnClick = {
                    Log.d(TAG, "Update clicked")
                    musiqueViewModel.scheduleWorker()
                }
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
                pager()
            }
            composable(route = Screen.Albums.name) {
                pager()
            }
            composable(route = Screen.History.name) {
                pager()
            }
            /*
            composable(route = Screen.Home.name) {
                SongListScreen(
                    musiqueViewModel,
                    onSongClick = onSongClick
                )
            }
            composable(route = Screen.Albums.name) {
                AlbumGridScreen(
                    musiqueViewModel,
                    onClick = onAlbumClick
                )
            }
            composable(route = Screen.History.name) {
                HistoryScreen(
                    musiqueViewModel,
                    onClick = onHistoryEntryClick
                )
            }
             */
            composable(route = Screen.SubList.name) {
                SongSubListScreen(
                    musiqueViewModel,
                    onSongClick = onSongClick,
                )
            }
        }
    }
}